package com.lingyifan.taskflowai;
import org.springframework.ai.converter.BeanOutputConverter; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient; import tools.jackson.databind.JsonNode; import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate; import java.util.*;
@Component
public class ModelGateway {
    private final String key; private final RestClient http; private final ObjectMapper json; private final BeanOutputConverter<TaskPlan> converter=new BeanOutputConverter<>(TaskPlan.class);
    public ModelGateway(@Value("${ai.api-key:}") String key,@Value("${ai.base-url:https://api.deepseek.com}") String base,ObjectMapper json){this.key=key;this.json=json;this.http=RestClient.builder().baseUrl(base).build();}
    public boolean mockMode(){return key==null||key.isBlank();}
    public String schema(){return converter.getJsonSchema();}
    public TaskPlan generate(String requirement){
        if(mockMode())return mock(requirement);
        String prompt="把需求拆成任务。"+converter.getFormat()+"\n需求："+requirement;
        Map<String,Object> body=Map.of("model","deepseek-chat","temperature",0.2,"messages",List.of(Map.of("role","user","content",prompt)));
        JsonNode response=http.post().uri("/chat/completions").header("Authorization","Bearer "+key).body(body).retrieve().body(JsonNode.class);
        return parse(response.path("choices").get(0).path("message").path("content").asText());
    }
    TaskPlan parse(String value){TaskPlan plan=converter.convert(value);if(plan==null||plan.title()==null||plan.title().isBlank()||plan.priority()==null||plan.steps()==null||plan.steps().isEmpty())throw new IllegalArgumentException("结构化输出缺少必要字段");return plan;}
    private TaskPlan mock(String requirement){String title=requirement.length()>60?requirement.substring(0,60):requirement;String clarify=requirement.length()<8?"需求比较短，请确认范围和完成标准":"";return new TaskPlan(title.isBlank()?"待确认任务":title,"MEDIUM",LocalDate.now().plusDays(7),List.of("确认需求范围","拆分实现步骤","完成后自测并记录结果"),clarify);}
}
