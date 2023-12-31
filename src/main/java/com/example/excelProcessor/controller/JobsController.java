package com.example.excelProcessor.controller;

import com.example.excelProcessor.util.StatusHolder;
import com.example.excelProcessor.util.StatusObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class JobsController {

   private final StatusHolder statusHolder;

   @Autowired
   public JobsController(StatusHolder statusHolder) {
      this.statusHolder = statusHolder;
   }

   @GetMapping("/jobs/get-job-status/{jobId}")
   public ResponseEntity<StatusObject> getProcessingStatus(@PathVariable Integer jobId){
      // Retrieve the processing status
      StatusObject status = statusHolder.getStatus(jobId);
      if (status.getStatusDescription().equals("Complete")){
         statusHolder.removeJobStatus(jobId);
      }
      return ResponseEntity.ok(status);
   }
}
