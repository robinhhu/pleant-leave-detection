package com.example.demo.controller;
import com.example.demo.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@RestController
@RequestMapping("/file")
public class FileController {
    private String filePath = "E:\\PlantLeave\\Files";
    private String result = "Infected";
    private String fileName = null;

    @GetMapping(value ="/info/{id}")
    public R info(@PathVariable("id") String id) throws IllegalStateException {
        if(fileName==null){
            System.out.println("null");
        }
        else {
            String re = runPy(fileName);
            System.out.println(re.equals("0"));
            if (re.equals("0")) {
                result = "Healthy";
            } else {
                result = "Infected";
            }
        }
        return R.ok().put("result", result);
    }

    @PostMapping("/upload")
    public R test(@RequestParam(value="file",required=false) MultipartFile image) throws IllegalStateException,IOException {
        System.out.println(image);
        byte[] bs = image.getBytes();
        int temp = 1;
        if (bs.length>0) {
            try {
                File cfile = new File(filePath);
                if (!cfile.exists()) {
                    cfile.mkdirs();
                }
                fileName= image.getOriginalFilename();
                InputStream ips = new ByteArrayInputStream(bs);
                File file=new File(filePath, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int n = 0;
                while ((n=ips.read(b))!=-1){
                    fos.write(b,0,n);
                }
                fos.flush();
                fos.close();
                ips.close();
                System.out.println("success!");

            } catch (Exception ex) {
                temp = 0;
                ex.printStackTrace();
            } finally {}
        } else {
            System.out.println("failed");
        }
        return R.ok().put("isSuccessful",true);
    }

    public String runPy(String fileName) {
        String[] arg = new String[] {"E:\\PlantLeave\\Files\\SVM\\venv\\Scripts\\python.exe", "E:\\PlantLeave\\Files\\SVM\\main.py",fileName};
        try {
            Process process = Runtime.getRuntime().exec(arg);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while((line=in.readLine())!=null){
                System.out.println(line);
                return line;
            }
            in.close();
            int read = process.waitFor();
            System.out.println("re"+ read);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return "1";
    }

}
