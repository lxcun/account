// package com.accounting.controller;

// import com.accounting.common.Result;
// import com.accounting.service.AuthService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/test")
// @RequiredArgsConstructor
// public class TestController {

//     private final AuthService authService;

//     @GetMapping("/generate-password")
//     public Result<Map<String, String>> generatePassword(@RequestParam String password) {
//         String encodedPassword = authService.encodePassword(password);
//         Map<String, String> result = new HashMap<>();
//         result.put("rawPassword", password);
//         result.put("encodedPassword", encodedPassword);
//         return Result.success(result);
//     }

//     @PostMapping("/test-login")
//     public Result<String> testLogin(@RequestParam String username, @RequestParam String password) {
//         try {
//             authService.encodePassword(password);
//             return Result.success("密码编码成功");
//         } catch (Exception e) {
//             return Result.error("失败: " + e.getMessage());
//         }
//     }
// }
