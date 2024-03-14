package br.com.fiap.gerente_itens.integracao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItensTestIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testeCadastrandoItensSucesso() {

        String randomWord = geraPalavraRandomica(8);
        String url = "http://localhost:" + port + "/itens";

        String requestBody = "{\"nome\":\"" + randomWord + "\"," +
                "\"valor\":\"10,00\"," +
                "\"qtd\":\"10\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String mensagem = jsonNode.get("Messagem").asText();

            Assert.assertEquals(mensagem, "Item CADASTRADO com sucesso.");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tentativaCadastrandoitemDuplicado_Test() {

        String randomWord = geraPalavraRandomica(8);
        String url = "http://localhost:" + port + "/itens";

        String requestBody = "{\"nome\":\""+ randomWord +"\"," +
                "\"valor\":\"11,00\"," +
                "\"qtd\":\"11\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String mensagem = jsonNode.get("Erro").asText();

            Assert.assertEquals(mensagem, "Item JÁ cadastrado.");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains("{\"Erro\": \"Item JÁ cadastrado.\"}"));
    }

    @Test
    public void deletaEndereco_SucessoTest() {

        String randomWord = geraPalavraRandomica(8);
        String url = "http://localhost:" + port + "/itens";

        String requestBody = "{\"nome\":\"" + randomWord + "\"," +
                "\"valor\":\"10,00\"," +
                "\"qtd\":\"10\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String mensagem = jsonNode.get("Messagem").asText();
            String id = jsonNode.get("id").asText();

            url = "http://localhost:" + port + "/itens/" + id;
            requestEntity = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assert.assertTrue(response.getBody() != null && response.getBody().contains("{\"Mensagem\": \"Item DELETADO com sucesso.\"}"));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deletaEndereco_FalhaTest() {
        String url = "http://localhost:" + port + "/itens/99968";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains("{\"Erro\": \"Item NÃO cadastrado.\"}"));
    }

    private static String geraPalavraRandomica(int length) {
        String allowedChars = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allowedChars.length());
            char randomChar = allowedChars.charAt(randomIndex);
            word.append(randomChar);
        }
        return word.toString();
    }


}
