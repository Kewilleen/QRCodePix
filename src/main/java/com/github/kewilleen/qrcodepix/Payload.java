package com.github.kewilleen.qrcodepix;

import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Kewilleen Gomes
 * Geração do PIX com base no padrão EMV para uso de QR Codes (EMV-QRCPS-MPM) Merchant Presented Mode
 * Onde o QRCode contém informações do beneficiário do pagamento e o contexto da transação.
 */
@Builder(toBuilder = true)
public class Payload {

    /**
     * Versão do payload QRCPS-MPM
     */
    public static final String ID_PAYLOAD_FORMAT_INDICATOR = "00";
    /**
     * Merchant Account Information
     */
    public static final String ID_MERCHANT_ACCOUNT_INFORMATION = "26";
    public static final String ID_MERCHANT_ACCOUNT_INFORMATION_GUI = "00";
    public static final String ID_MERCHANT_ACCOUNT_INFORMATION_KEY = "01";
    public static final String ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION = "02";
    public static final String ID_MERCHANT_CATEGORY_CODE = "52"; // Merchant Category Code
    /**
     * Transações
     */
    public static final String ID_TRANSACTION_CURRENCY = "53";
    public static final String ID_TRANSACTION_AMOUNT = "54";
    /**
     * Dados do beneficiário/recebedor
     */
    public static final String ID_COUNTRY_CODE = "58";
    public static final String ID_MERCHANT_NAME = "59";
    public static final String ID_MERCHANT_CITY = "60";
    public static final String ID_ADDITIONAL_DATA_FIELD_KEY = "62"; // Reference label
    public static final String ID_ADDITIONAL_DATA_FIELD_TRANSACTION = "05"; // Unreserved Templates - GUI
    public static final String ID_CRC16 = "63"; // 0x34D1

    private final String pixKey;
    private final String description;
    private final String name;
    private final String city;
    private final String transaction;
    private final BigDecimal amount;

    private String getFormatSize(String id, String value) {
        String size = String.format("%02d", value.length());
        return id + size + value;
    }

    private String getAddictionalDataFieldTemplate() {
        String tx = getFormatSize(ID_ADDITIONAL_DATA_FIELD_TRANSACTION, transaction);
        return getFormatSize(ID_ADDITIONAL_DATA_FIELD_KEY, tx);
    }

    private String getMerchantAccountInformation() {
        String gui = getFormatSize(ID_MERCHANT_ACCOUNT_INFORMATION_GUI, "br.gov.bcb.pix");
        String key = getFormatSize(ID_MERCHANT_ACCOUNT_INFORMATION_KEY, this.pixKey);
        String desc = this.description != null && this.description.length() > 0 ?
                getFormatSize(ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION, this.description) : "";
        return getFormatSize(ID_MERCHANT_ACCOUNT_INFORMATION, gui + key + desc);
    }

    public String getPayload() {
        String payload = getFormatSize(ID_PAYLOAD_FORMAT_INDICATOR, "01")
                + getMerchantAccountInformation()
                + getFormatSize(ID_MERCHANT_CATEGORY_CODE, "0000")
                + getFormatSize(ID_TRANSACTION_CURRENCY, "986")
                + getFormatSize(ID_TRANSACTION_AMOUNT, amount.setScale(2, RoundingMode.UP).toString())
                + getFormatSize(ID_COUNTRY_CODE, "BR")
                + getFormatSize(ID_MERCHANT_NAME, name)
                + getFormatSize(ID_MERCHANT_CITY, city)
                + getAddictionalDataFieldTemplate()
                + ID_CRC16 + "04";
        int crcRes = getCRC16(payload.getBytes());
        return payload + Integer.toHexString(crcRes).toUpperCase();
    }

    public int getCRC16(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x1021;
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
            }
        }
        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
    }
}
