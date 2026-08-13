package com.lingyifan.taskflowai;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import org.springframework.web.client.RestClient;
import java.time.*; import java.util.*; import java.util.concurrent.*;
@Service
public class PlanService {
    public record Analysis(UUID id,TaskPlan plan,String mode,int attempts,long elapsedMs,String status,String failureReason){}
    private final ModelGateway model; private final RestClient taskflow; private final Map<UUID,Analysis> store=new ConcurrentHashMap<>();
    public PlanService(ModelGateway model,@Value("${taskflow.base-url:http://localhost:8080}")String base){this.model=model;this.taskflow=RestClient.builder().baseUrl(base).build();}
    public Analysis analyze(String requirement){long start=System.nanoTime();RuntimeException last=null;for(int i=1;i<=3;i++)try{TaskPlan p=model.generate(requirement);validate(p);Analysis a=new Analysis(UUID.randomUUID(),p,model.mockMode()?"mock":"deepseek",i,elapsed(start),"WAITING_CONFIRMATION","");store.put(a.id(),a);return a;}catch(RuntimeException e){last=e;}throw new IllegalStateException("模型输出连续三次不合格",last);}
    public Object confirm(UUID id,Long projectId,String token){Analysis a=Optional.ofNullable(store.get(id)).orElseThrow(()->new IllegalArgumentException("分析记录不存在"));Map<String,Object> body=new LinkedHashMap<>();body.put("projectId",projectId);body.put("title",a.plan().title());body.put("description",String.join("\n",a.plan().steps()));body.put("priority",a.plan().priority());body.put("dueAt",a.plan().dueDate()+"T23:59:00");Object result=taskflow.post().uri("/api/tasks").header("Authorization","Bearer "+token).body(body).retrieve().body(Object.class);store.put(id,new Analysis(a.id(),a.plan(),a.mode(),a.attempts(),a.elapsedMs(),"CREATED_IN_TASKFLOW",""));return result;}
    public Collection<Analysis> all(){return store.values();}
    private void validate(TaskPlan p){if(p==null||blank(p.title())||!Set.of("LOW","MEDIUM","HIGH").contains(p.priority())||p.steps()==null||p.steps().isEmpty())throw new IllegalArgumentException("结构化输出缺少必要字段");}
    private boolean blank(String s){return s==null||s.isBlank();} private long elapsed(long s){return TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-s);}
}
