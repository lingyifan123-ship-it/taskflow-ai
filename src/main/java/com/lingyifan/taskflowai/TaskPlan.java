package com.lingyifan.taskflowai;
import java.time.LocalDate; import java.util.List;
public record TaskPlan(String title,String priority,LocalDate dueDate,List<String> steps,String clarification) {}
