# QRCodePix
Sistema simples para a geração de pix gerenciada pelo banco central, atualmente o código é apenas para brasileiros.

[![img.png](img.png)]

## Como usar?
A utilização para o PIX requer obrigatóriamente os seguintes atributos a serem preenchidos:

```java
Payload payload = Payload.builder()
.pixKey("CHAVEPIX")
.description("DESCRICAO")
.name("RECEPTOR DO PIX")
.city("CIDADE")
.amount(new BigDecimal("1.0"))
.transaction("REF.TRANSACTION").build();
// Gerar o código para o pagamento de PIX
String qrcode = payload.getPayload();
```

### QRCode
Documentação para a geração do QRCode http://code.google.com/p/zxing/

Para facilitar a utilização da bilioteca: https://github.com/kenglxn/QRGen

```` java
ByteArrayOutputStream byteArrayOutputStream = QRCode
                .from(qrcode)
                .to(ImageType.JPG)
                .withSize(512, 512).stream();

        try (OutputStream outputStream = new FileOutputStream("qrcode.jpg")) {
            byteArrayOutputStream.writeTo(outputStream);
        }
````

Deixo a documentação utilizada para a geração.