package platform;


import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;


import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Validated
public class TaskController {

    @Qualifier("freeMarkerConfiguration")
    @Autowired
    Configuration cfg;

    @Autowired
    CodeSnippetService codeSnippetService;

    @PostMapping("/api/code/new")
    public ResponseEntity<?> newCode(@Valid @RequestBody CodeSnippet codeReceived) {
        CodeSnippet codeToReturn = new CodeSnippet();
        codeToReturn.setCode(codeReceived.getCode());
        codeToReturn.setDateTime(LocalDateTime.now());
        codeToReturn.setTime(codeReceived.getTime());
        codeToReturn.setTimeOfExpire();
        codeToReturn.setViews(codeReceived.getViews());
        codeToReturn.setSecret(codeReceived.getViews() > 0 || codeReceived.getTime() > 0);
        codeSnippetService.saveCodeSnippet(codeToReturn);
        Map<String, String> UuidOut = new HashMap<>(1) {{
            put("id", codeToReturn.getId());
        }};
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(UuidOut, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/api/code/{id}")
    public ResponseEntity<?> getCodeInApi(@PathVariable String id) {
        if (codeSnippetService.getCodeSnippetById(id).isPresent()) {
            if(codeSnippetService.getCodeSnippetById(id).get().getisSecret()) {
                long timeLeft = calculateTimeLeft(codeSnippetService.getCodeSnippetById(id).get());
                if (codeSnippetService.getCodeSnippetById(id).get().isActive() || codeSnippetService.getCodeSnippetById(id).get().getViews() > 0) {
                    CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                    if (codeToReturn.getViews() > 0) {
                        codeToReturn.setViews(codeToReturn.getViews() - 1);
                        codeSnippetService.saveCodeSnippet(codeToReturn);
                    }
                    Map<String, Object> bodyToReturn = new LinkedHashMap<>();
                    bodyToReturn.put("code", codeToReturn.getCode());
                    bodyToReturn.put("date", codeToReturn.getDateTime());
                    bodyToReturn.put("time", timeLeft);
                    bodyToReturn.put("views", codeToReturn.getViews());
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "application/json");
                    return new ResponseEntity<Object>(bodyToReturn, responseHeaders, HttpStatus.OK);
                } else {
                    codeSnippetService.deleteCodeById(id);
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "application/json");
                    return new ResponseEntity<Object>("NOT FOUND!", responseHeaders, HttpStatus.NOT_FOUND);
                }
            } else {
                CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                Map<String, Object> bodyToReturn = new LinkedHashMap<>();
                bodyToReturn.put("code", codeToReturn.getCode());
                bodyToReturn.put("date", codeToReturn.getDateTime());
                bodyToReturn.put("time", codeToReturn.getTime());
                bodyToReturn.put("views", codeToReturn.getViews());
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Content-Type", "application/json");
                return new ResponseEntity<Object>(bodyToReturn, responseHeaders, HttpStatus.OK);
            }
        } else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/json");
            return new ResponseEntity<Object>("NOT FOUND!", responseHeaders, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/code/{id}")
    public ResponseEntity<?> getCodeInHtml(@PathVariable String id, Model model) throws IOException, TemplateException {
        if (codeSnippetService.getCodeSnippetById(id).isPresent()) {
            if(codeSnippetService.getCodeSnippetById(id).get().getisSecret()) {
                long timeLeft = calculateTimeLeft(codeSnippetService.getCodeSnippetById(id).get());
                if(codeSnippetService.getCodeSnippetById(id).get().isActive() && codeSnippetService.getCodeSnippetById(id).get().getViews() > 0){
                    CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                    if (codeToReturn.getViews() > 0) {
                        codeToReturn.setViews(codeToReturn.getViews() - 1);
                        if(codeToReturn.getViews() == 0){
                            codeSnippetService.deleteCodeById(id);
                        } else {
                            codeSnippetService.saveCodeSnippet(codeToReturn);
                        }
                    }
                    model.addAttribute("code", codeToReturn);
                    model.addAttribute("timeLeft", timeLeft);
                    StringWriter stringWriter = new StringWriter();
                    cfg.getTemplate("codeBoth.ftlh").process(model, stringWriter);
                    String body = stringWriter.toString();
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
                }
                else if (codeSnippetService.getCodeSnippetById(id).get().getViews() > 0){
                    CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                    if (codeToReturn.getViews() > 0) {
                        codeToReturn.setViews(codeToReturn.getViews() - 1);
                        if(codeToReturn.getViews() == 0){
                            codeSnippetService.deleteCodeById(id);
                        } else {
                            codeSnippetService.saveCodeSnippet(codeToReturn);
                        }
                    }
                    model.addAttribute("code", codeToReturn);
                    StringWriter stringWriter = new StringWriter();
                    cfg.getTemplate("codeViews.ftlh").process(model, stringWriter);
                    String body = stringWriter.toString();
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
                }
                else if (codeSnippetService.getCodeSnippetById(id).get().isActive()) {
                    CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                    model.addAttribute("code", codeToReturn);
                    model.addAttribute("timeLeft", timeLeft);
                    StringWriter stringWriter = new StringWriter();
                    cfg.getTemplate("codeTime.ftlh").process(model, stringWriter);
                    String body = stringWriter.toString();
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
                } else {
                    codeSnippetService.deleteCodeById(id);
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "text/html");
                    return new ResponseEntity<Object>("NOT FOUND!", responseHeaders, HttpStatus.NOT_FOUND);
                }
            } else {
                CodeSnippet codeToReturn = codeSnippetService.getCodeSnippetById(id).get();
                model.addAttribute("code", codeToReturn);
                StringWriter stringWriter = new StringWriter();
                cfg.getTemplate("code.ftlh").process(model, stringWriter);
                String body = stringWriter.toString();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Content-Type", "text/html");
                return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
            }
        } else {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "text/html");
            return new ResponseEntity<Object>("NOT FOUND!", responseHeaders, HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/api/code/latest")
    public ResponseEntity<?> getLatestCodeInApi() {
        List<CodeSnippet> listOfAllCodes = codeSnippetService.getAllCodes();
        List<Object> listToReturn = new ArrayList<>();
        for (int i = 0; i < listOfAllCodes.size(); i++) {
            Map<String, Object> bodyToReturn = new LinkedHashMap<>();
            CodeSnippet codeToReturn = listOfAllCodes.get(i);
            bodyToReturn.put("code", codeToReturn.getCode());
            bodyToReturn.put("date", codeToReturn.getDateTime());
            bodyToReturn.put("time", codeToReturn.getTime());
            bodyToReturn.put("views", codeToReturn.getViews());
            listToReturn.add(bodyToReturn);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(listToReturn, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/code/latest")
    public ResponseEntity<?> getLatestCodeInHtml(Model model) throws IOException, TemplateException {
        List<CodeSnippet> listOfAllCodes = codeSnippetService.getAllCodes();
        model.addAttribute("listOfCodes", listOfAllCodes);
        StringWriter stringWriter = new StringWriter();
        cfg.getTemplate("codeList.ftlh").process(model, stringWriter);
        String body = stringWriter.toString();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/html");
        return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
    }



    @GetMapping("/code/new")
    public ResponseEntity<?> newCodeViaHtml(Model model) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        cfg.getTemplate("addingTemplate.ftlh").process(model, stringWriter);
        String body = stringWriter.toString();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/html");
        return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
    }

    @Bean
    public Configuration getCfg() {
        return new Configuration(Configuration.VERSION_2_3_30);
    }

    long calculateTimeLeft(CodeSnippet codeSnippet){;
        LocalDateTime timeOfExpire = codeSnippet.getTimeOfExpire();
        LocalDateTime timeNow = LocalDateTime.now();
        if (!Duration.between(timeNow, timeOfExpire).isNegative()) {
            codeSnippet.setActive(true);
            return Duration.between(timeNow, timeOfExpire).getSeconds();
        } else {
            codeSnippet.setActive(false);
            return 0;
        }
    }

}
