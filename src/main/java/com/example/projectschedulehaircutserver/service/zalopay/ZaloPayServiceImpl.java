package com.example.projectschedulehaircutserver.service.zalopay;

import com.example.projectschedulehaircutserver.request.PaymentRequest;
import com.example.projectschedulehaircutserver.service.order.OrderService;
import com.example.projectschedulehaircutserver.utils.HmacUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ZaloPayServiceImpl implements ZaloPayService {

    private final Environment env;
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> createPayment(PaymentRequest request) throws Exception {
        Map<String, Object> params = buildPaymentParams(request);
        params.put("mac", generateMac(params));

        // Debug log
        System.out.println("Request Params: " + params);
        System.out.println("MAC Data: " + buildMacData(params));

        String endpoint = env.getProperty("zalopay.endpoint");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(endpoint);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(objectMapper.writeValueAsString(params)));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("ZaloPay Response: " + responseBody); // Debug log
                return objectMapper.readValue(responseBody, Map.class);
            }
        }
    }

    @Override
    public void processPaymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = extractRequestParams(request);

        try {
            // Verify MAC
            String receivedMac = fields.get("mac");
            if (receivedMac == null) throw new Exception("Missing MAC");

            String calculatedMac = generateCallbackMac(fields);
            if (!calculatedMac.equals(receivedMac)) throw new Exception("Invalid MAC");

            // Process payment status
            String returnCode = fields.get("return_code");
            String appTransId = fields.get("app_trans_id");
            String status = "1".equals(returnCode) ? "success" : "failure";

            if ("success".equals(status)) {
                updateOrderStatus(appTransId);
            }

            // Build redirect URL
            String redirectUrl = buildRedirectUrl(
                    status,
                    fields.get("zp_trans_id"),
                    fields.get("amount")
            );
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(buildRedirectUrl("error", null, null));
        }
    }

    private Map<String, Object> buildPaymentParams(PaymentRequest request) {
        long timestamp = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("app_id", env.getProperty("zalopay.app_id"));
        params.put("app_user", "User_" + request.getOrderReferenceId());
        params.put("app_time", timestamp);
        params.put("amount", (int) request.getAmount());
        params.put("app_trans_id", generateAppTransId(request.getOrderReferenceId()));
        params.put("description", "Payment for booking " + request.getOrderReferenceId());
        params.put("bank_code", "");
//        params.put("embed_data", "{\"merchantinfo\":\"haircut_booking\"}");
//        params.put("item", "[{\"itemid\":\"service\",\"itemprice\":" + (int) request.getAmount() + "}]");
        params.put("callback_url", env.getProperty("zalopay.callback_url"));

        return params;
    }

    private String generateAppTransId(String orderRefId) {
        return new SimpleDateFormat("yyMMdd").format(new Date()) + "_" + orderRefId;
    }

    private String generateMac(Map<String, Object> params) throws Exception {
        String key1 = env.getProperty("zalopay.key1");
        return HmacUtil.HMacHex(key1, buildMacData(params));
    }

    private String buildMacData(Map<String, Object> params) {
        return String.format("%s|%s|%s|%s|%s|%s|%s",
                params.get("app_id"),
                params.get("app_trans_id"),
                params.get("app_user"),
                params.get("amount"),
                params.get("app_time"),
                params.get("embed_data"),
                params.get("item")
        );
    }

    private String generateCallbackMac(Map<String, String> fields) throws Exception {
        String key2 = env.getProperty("zalopay.key2");
        String data = String.format("%s|%s|%s|%s|%s|%s",
                fields.get("app_id"),
                fields.get("app_trans_id"),
                fields.get("app_user"),
                fields.get("amount"),
                fields.get("app_time"),
                fields.get("status")
        );
        return HmacUtil.HMacHex(key2, data);
    }

    private Map<String, String> extractRequestParams(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(param -> fields.put(param, request.getParameter(param)));
        return fields;
    }

    private String buildRedirectUrl(String status, String transactionId, String amount) {
        return String.format("%s?status=%s&transactionId=%s&amount=%s",
                env.getProperty("zalopay.return_url"),
                status,
                transactionId,
                amount
        );
    }

    private void updateOrderStatus(String appTransId) {
        try {
            String[] parts = appTransId.split("_");
            if (parts.length > 1) {
                String bookingId = parts[parts.length - 1];
                orderService.updateBookingStatus(Integer.parseInt(bookingId), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}