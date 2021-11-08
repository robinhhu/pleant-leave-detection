package com.example.demo.controller;

import com.example.demo.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * 信息
     */
    @GetMapping(value = "/info/{id}")
    public R info(@PathVariable("id") String id) {

        return R.ok().put("result", "true");
    }

    @PostMapping("/upload")
    public R test(@RequestParam String rootId) {

        return R.ok().put("isSuccessful", true);
    }

}
