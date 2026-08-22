package com.lingyifan.taskflowai;
import org.junit.jupiter.api.*; import tools.jackson.databind.json.JsonMapper; import static org.junit.jupiter.api.Assertions.*;
class ModelGatewayTests {
 ModelGateway gateway=new ModelGateway("","https://api.deepseek.com",JsonMapper.builder().findAndAddModules().build());
 @Test void fixedCasesUseMockSafely(){for(String input:new String[]{"做一个登录接口","帮我弄一下","x".repeat(3000),"模拟模型失败时仍可运行"}){TaskPlan p=gateway.generate(input);assertNotNull(p.title());assertFalse(p.steps().isEmpty());}assertTrue(gateway.mockMode());}
 @Test void missingFieldFailsSchemaConversion(){assertThrows(RuntimeException.class,()->gateway.parse("{\"priority\":\"HIGH\"}"));}
 @Test void schemaContainsRequiredFields(){String schema=gateway.schema();assertTrue(schema.contains("title"));assertTrue(schema.contains("steps"));}
}
