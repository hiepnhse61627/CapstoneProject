package com.capstone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SubjectController {
    @RequestMapping("/subject")
    public String Index() {
        return "UploadSubject";
    }

    @RequestMapping(value = "/subject", method = RequestMethod.POST)
    public @ResponseBody String Upload(@RequestParam("file") MultipartFile file) {
//        System.out.println("File uploaded: " + file.getName());
        return "{ 'success': true }";
    }
}
