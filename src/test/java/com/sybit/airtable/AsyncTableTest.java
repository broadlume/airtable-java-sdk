package com.sybit.airtable;

import java.util.Arrays;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybit.airtable.exception.AirtableMappingException;
import com.sybit.airtable.internal.http.AirtableHttpClient;
import com.sybit.airtable.test.DummyRow;
import com.sybit.airtable.vo.Delete;
import com.sybit.airtable.vo.Record;
import com.sybit.airtable.vo.Records;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.asynchttpclient.Response;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AsyncTableTest {

    private AirtableHttpClient httpClient = mock(AirtableHttpClient.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Response response = mock(Response.class);
    private AsyncTable<DummyRow> table = new AsyncTable<>("https://localhost/base/table", "1234",
            DummyRow.class, httpClient, objectMapper);

    /**
     * Should build the correct request, parse out the record JSON, and return a Publisher containing each record
     */
    @Test
    public void selectTest() throws InterruptedException, JsonProcessingException {
        when(httpClient.execute(argThat(arg ->
                arg != null &&
                        "https://localhost/base/table".equals(arg.getUrl()) &&
                        "GET".equals(arg.getMethod()) &&
                        arg.getHeaders().size() == 2 &&
                        arg.getHeaders().contains("Accept", "application/json", false) &&
                        arg.getHeaders().contains("Authorization", "Bearer 1234", false))))
                .thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        Records<DummyRow> records = new Records<>(Arrays.asList(
                Record.of("123", new DummyRow("1", "name", 12), "today"),
                Record.of("456", new DummyRow("2", "name2", 13), "tomorrow")), null);
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(records));

        Flowable.fromPublisher(table.select())
                .test().await()
                .assertValueCount(2)
                .assertValueAt(0, Record.of("123", new DummyRow("1", "name", 12), "today"))
                .assertValueAt(1, Record.of("456", new DummyRow("2", "name2", 13), "tomorrow"))
                .assertComplete().assertNoErrors();
    }

    /**
     * When the response contains an offset value, should append the next set of records to the current set
     */
    @Test
    public void selectMultiplePagesTest() throws InterruptedException, JsonProcessingException {
        when(httpClient.execute(argThat(arg -> arg != null && "https://localhost/base/table".equals(arg.getUrl()))))
                .thenReturn(Single.just(response));
        Response response2 = mock(Response.class);
        when(httpClient.execute(argThat(arg -> arg != null && "https://localhost/base/table?offset=abc".equals(arg.getUrl()))))
                .thenReturn(Single.just(response2));

        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(new Records<>(Arrays.asList(
                Record.of("123", new DummyRow("1", "name", 12), "today"),
                Record.of("456", new DummyRow("2", "name2", 34), "tomorrow")), "abc")));
        when(response2.getStatusCode()).thenReturn(200);
        when(response2.getResponseBody()).thenReturn(objectMapper.writeValueAsString(new Records<>(Arrays.asList(
                Record.of("789", new DummyRow("3", "name3", 56), "never"),
                Record.of("098", new DummyRow("4", "name4", 78), "yesterday")), null)));

        Flowable.fromPublisher(table.select())
                .test().await()
                .assertValueCount(4)
                .assertValueAt(0, Record.of("123", new DummyRow("1", "name", 12), "today"))
                .assertValueAt(1, Record.of("456", new DummyRow("2", "name2", 34), "tomorrow"))
                .assertValueAt(2, Record.of("789", new DummyRow("3", "name3", 56), "never"))
                .assertValueAt(3, Record.of("098", new DummyRow("4", "name4", 78), "yesterday"))
                .assertComplete().assertNoErrors();
    }

    /**
     * Any exceptions when parsing the JSON should be wrapped in an AirtableParsingException
     */
    @Test
    public void selectParsingErrorTest() throws InterruptedException {
        when(httpClient.execute(any())).thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn("1");

        Flowable.fromPublisher(table.select())
                .test().await()
                .assertError(AirtableMappingException.class);
    }

    /**
     * Should build the correct request, parse the response JSON, and return a Publisher containing the single response
     * record
     */
    @Test
    public void findTest() throws JsonProcessingException, InterruptedException {
        when(httpClient.execute(argThat(arg ->
                arg != null &&
                        "https://localhost/base/table/abc123".equals(arg.getUrl()) &&
                        "GET".equals(arg.getMethod()) &&
                        arg.getHeaders().size() == 2 &&
                        arg.getHeaders().contains("Accept", "application/json", false) &&
                        arg.getHeaders().contains("Authorization", "Bearer 1234", false))))
                .thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        Record<DummyRow> record = Record.of("123", new DummyRow("1", "name", 12), "today");
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(record));

        Single.fromPublisher(table.find("abc123"))
                .test().await()
                .assertValue(record)
                .assertComplete().assertNoErrors();
    }

    /**
     * Any exceptions when parsing the JSON should be wrapped in an AirtableParsingException
     */
    @Test
    public void findParsingErrorTest() throws InterruptedException {
        when(httpClient.execute(any())).thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn("1");

        Flowable.fromPublisher(table.find("123abc"))
                .test().await()
                .assertError(AirtableMappingException.class);
    }

    /**
     * Should build the correct request, marshall the record to JSON, parse the response JSON, and return a Publisher
     * containing the single response record
     */
    @Test
    public void createTest() throws JsonProcessingException, InterruptedException {
        DummyRow item = new DummyRow("3", "dummy", 4);
        byte[] bytes = objectMapper.writeValueAsBytes(Record.of(item));
        when(httpClient.execute(argThat(arg ->
                arg != null &&
                        "https://localhost/base/table".equals(arg.getUrl()) &&
                        "POST".equals(arg.getMethod()) &&
                        arg.getHeaders().size() == 3 &&
                        arg.getHeaders().contains("Accept", "application/json", false) &&
                        arg.getHeaders().contains("Content-Type", "application/json", false) &&
                        arg.getHeaders().contains("Authorization", "Bearer 1234", false) &&
                        Arrays.equals(bytes, arg.getByteData()))))
                .thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        Record<DummyRow> record = Record.of("abc", item, "now");
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(record));

        Single.fromPublisher(table.create(item))
                .test().await()
                .assertValue(record)
                .assertComplete().assertNoErrors();
    }

    /**
     * Should build the correct request, marshall the record to JSON, parse the response JSON, and return a Publisher
     * containing the single response record
     */
    @Test
    public void updateTest() throws JsonProcessingException, InterruptedException {
        DummyRow item = new DummyRow("1", "name", 2);
        byte[] bytes = objectMapper.writeValueAsBytes(Record.of(item));
        when(httpClient.execute(argThat(arg ->
                arg != null &&
                        "https://localhost/base/table/abc".equals(arg.getUrl()) &&
                        "PATCH".equals(arg.getMethod()) &&
                        arg.getHeaders().size() == 3 &&
                        arg.getHeaders().contains("Accept", "application/json", false) &&
                        arg.getHeaders().contains("Content-Type", "application/json", false) &&
                        arg.getHeaders().contains("Authorization", "Bearer 1234", false) &&
                        Arrays.equals(bytes, arg.getByteData()))))
                .thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        Record<DummyRow> record = Record.of("abc", item, "now");
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(record));

        Single.fromPublisher(table.update("abc", item))
                .test().await()
                .assertValue(record)
                .assertComplete().assertNoErrors();
    }

    /**
     * Should build the correct request, parse the response JSON, and return a Publisher containing the single delete
     * result
     */
    @Test
    public void deleteTest() throws InterruptedException, JsonProcessingException {
        when(httpClient.execute(argThat(arg ->
                arg != null &&
                        "https://localhost/base/table/abc".equals(arg.getUrl()) &&
                        "DELETE".equals(arg.getMethod()) &&
                        arg.getHeaders().size() == 2 &&
                        arg.getHeaders().contains("Accept", "application/json", false) &&
                        arg.getHeaders().contains("Authorization", "Bearer 1234", false))))
                .thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        Delete delete = new Delete(true, "abc");
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(delete));

        Single.fromPublisher(table.delete("abc"))
                .test().await()
                .assertValue(true)
                .assertComplete().assertNoErrors();
    }

    /**
     * Any exceptions when parsing the JSON should be wrapped in an AirtableParsingException
     */
    @Test
    public void deleteParsingErrorTest() throws InterruptedException {
        when(httpClient.execute(any())).thenReturn(Single.just(response));
        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn("1");

        Flowable.fromPublisher(table.delete("123abc"))
                .test().await()
                .assertError(AirtableMappingException.class);
    }
}
