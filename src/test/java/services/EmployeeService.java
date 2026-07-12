package services;

import io.restassured.response.Response;
import utils.ConfigReader;

/**
 * Local json-server used for the POST and JSON-schema demos.
 */
public class EmployeeService extends BaseService {

    private static final String EMPLOYEES = "/employees";
    private static final String STORE = "/store";

    public EmployeeService() {
        super(ConfigReader.get("jsonserver.baseUrl"));
    }

    public Response createEmployee(Object payload) {
        return post(EMPLOYEES, payload);
    }

    public Response getStore() {
        return get(STORE);
    }
}
