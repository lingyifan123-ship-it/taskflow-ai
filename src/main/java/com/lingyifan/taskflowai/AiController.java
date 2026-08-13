package com.lingyifan.taskflowai;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/ai")
public class AiController {
    record AnalyzeInput(@NotBlank @Size(max=10000) String requirement){} record ConfirmInput(@NotNull Long projectId,@NotBlank String taskflowToken){}
    private final PlanService plans; private final ModelGateway model; public AiController(PlanService plans,ModelGateway model){this.plans=plans;this.model=model;}
    @PostMapping("/analyze") PlanService.Analysis analyze(@Valid @RequestBody AnalyzeInput in){return plans.analyze(in.requirement());}
    @PostMapping("/{id}/confirm") Object confirm(@PathVariable UUID id,@Valid @RequestBody ConfirmInput in){return plans.confirm(id,in.projectId(),in.taskflowToken());}
    @GetMapping("/runs") Collection<PlanService.Analysis> runs(){return plans.all();}
    @GetMapping("/schema") Map<String,Object> schema(){return Map.of("jsonSchema",model.schema(),"mockMode",model.mockMode());}
}
