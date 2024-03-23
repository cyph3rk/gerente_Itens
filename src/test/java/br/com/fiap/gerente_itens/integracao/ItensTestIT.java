package br.com.fiap.gerente_itens.integracao;

import br.com.fiap.gerente_itens.model.Itens;
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

    private String token;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ERRO = "Item NÃO encontrado.";
    private static final String SUCESSO = "Item CADASTRADO com sucesso.";
    private static final String DELETE_SUCESSO = "Item DELETADO com sucesso";
    private static final String ALTERADO_SUCESSO = "Item ALTERADO com sucesso.";
    private static final String JA_CADASTRADO = "Item JÁ cadastrado.";
    private static final String SEM_ESTOQUE = "Solicitação maior que o estoque disponível";
    @Test
    void testeCadastrandoItensSucesso() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        String mensagem = jsonNode.get("Messagem").asText();
        Assert.assertEquals(SUCESSO, mensagem);

    }

    @Test
    void tentativaCadastrandoitemDuplicado_Test() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Assert.assertEquals(JA_CADASTRADO, response.getBody());

        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains(JA_CADASTRADO));
    }

    @Test
    void deletaEndereco_SucessoTest() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());


        objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String id = jsonNode.get("id").asText();

        url = "http://localhost:" + port + "/api/itens/" + id;
        requestEntity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains(DELETE_SUCESSO));

    }

    @Test
    void deletaEndereco_FalhaTest() {

        geraTokenTest();

        String url = "http://localhost:" + port + "/api/itens/99968";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains(ERRO));
    }

    @Test
    void pesquisaItensPorId_SucessoTest() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String mensagem = jsonNode.get("Messagem").asText();
        String id = jsonNode.get("id").asText();

        Assert.assertEquals(SUCESSO, mensagem);

        url = "http://localhost:" + port + "/api/itens/" + id;
        requestEntity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String resp = "{\"id\":" + id + "," +
                "\"nome\":\"" + randomWord + "\"," +
                "\"valor\":\"10,00\"," +
                "\"estoque\":\"10\"}";

        Assert.assertTrue(response.getBody() != null && response.getBody().contains(resp));

    }

    @Test
    void pesquisaItensPorId_FalhaTest() {
        geraTokenTest();
        String url = "http://localhost:" + port + "/api/itens/99968";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains(ERRO));
    }

    @Test
    void soma_Item_Estoque_SucessoTest() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());


        objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String mensagem = jsonNode.get("Messagem").asText();
        String id = jsonNode.get("id").asText();

        Assert.assertEquals(SUCESSO, mensagem);

        url = "http://localhost:" + port + "/api/itens/soma/" + id + "/10";

        requestEntity = new HttpEntity<>(requestBody, headers);
        response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        Assert.assertEquals(ALTERADO_SUCESSO, response.getBody());

        url = "http://localhost:" + port + "/api/itens/" + id;
        requestEntity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String resp = "{\"id\":" + id + "," +
                "\"nome\":\"" + randomWord + "\"," +
                "\"valor\":\"10,00\"," +
                "\"estoque\":\"20\"}";

        Assert.assertTrue(response.getBody() != null && response.getBody().contains(resp));

    }

    @Test
    void diminui_Item_Estoque_SucessoTest() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());


        objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String mensagem = jsonNode.get("Messagem").asText();
        String id = jsonNode.get("id").asText();

        Assert.assertEquals(SUCESSO, mensagem);

        url = "http://localhost:" + port + "/api/itens/diminui/" + id + "/1";

        requestEntity = new HttpEntity<>(requestBody, headers);
        response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        Assert.assertEquals(ALTERADO_SUCESSO, response.getBody());

        url = "http://localhost:" + port + "/api/itens/" + id;
        requestEntity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String resp = "{\"id\":" + id + "," +
                "\"nome\":\"" + randomWord + "\"," +
                "\"valor\":\"10,00\"," +
                "\"estoque\":\"9\"}";

        Assert.assertTrue(response.getBody() != null && response.getBody().contains(resp));

    }

    @Test
    void diminui_Item_Estoque_FalhaTest() throws JsonProcessingException {

        geraTokenTest();

        String randomWord = geraPalavraRandomica();
        String url = "http://localhost:" + port + "/api/itens";

        Itens itens = geraItem(randomWord);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(itens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());


        objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String mensagem = jsonNode.get("Messagem").asText();
        String id = jsonNode.get("id").asText();

        Assert.assertEquals(SUCESSO, mensagem);

        url = "http://localhost:" + port + "/api/itens/diminui/" + id + "/11";

        requestEntity = new HttpEntity<>(requestBody, headers);
        response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody() != null && response.getBody().contains(SEM_ESTOQUE));

    }

    Itens geraItem(String nome) {
        return new Itens(nome, "10,00", "10");
    }

    void geraTokenTest() {

        String url = "http://localhost:8082/auth/login";

        String requestBody = "{\"login\":\"testedev\"," +
                "\"password\":\"testedev\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            token = jsonNode.get("token").asText();

            Assert.assertNotNull(token);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private static String geraPalavraRandomica() {
        int length = 8;
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
