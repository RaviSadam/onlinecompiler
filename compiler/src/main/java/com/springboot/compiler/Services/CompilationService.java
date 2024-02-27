package com.springboot.compiler.Services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.springboot.compiler.Dtos.CompilationResponse;
import com.springboot.compiler.Dtos.CompileRequest;
import com.springboot.compiler.Models.Status;


@Component
public class CompilationService {

    private final static Map<String,String> EXTENSION_MAP;
    private final static Map<String,List<ProcessBuilder>> LANGUAGE_PROCESSBUILDERS;

    
    private final ExecutorService executorService=Executors.newFixedThreadPool(5);


    @Value("${submission.path}")
    private String path;

    static{
        EXTENSION_MAP=new HashMap<>();
        LANGUAGE_PROCESSBUILDERS=new HashMap<>();

        //language corresponding compilation and execution commands
        LANGUAGE_PROCESSBUILDERS.put("python",List.of(new ProcessBuilder("python","Main.py")));
        LANGUAGE_PROCESSBUILDERS.put("java",List.of(new ProcessBuilder("javac","Main.java"),new ProcessBuilder("java","Main")));
        LANGUAGE_PROCESSBUILDERS.put("c++",List.of(new ProcessBuilder("g++","Main.cpp"),new ProcessBuilder("Main.exe")));
        LANGUAGE_PROCESSBUILDERS.put("c",List.of(new ProcessBuilder("gcc","Main.c"),new ProcessBuilder("Main.exe")));


        //language corresponding extensions
        EXTENSION_MAP.put("python", ".py");
        EXTENSION_MAP.put("java", ".java");
        EXTENSION_MAP.put("c", ".c");
        EXTENSION_MAP.put("c++", ".cpp");        
    }
    
    public CompilationResponse compileAndRunCode(CompileRequest compileRequest){
        Path path=this.getPath();
        File directory=null,inputFile=null,sourceFile=null,outputFile=null;

        
        //making/creating new directory in /src/main/resources/ for compilation purpose
        try {
            directory=this.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating the source,output files
        try {
            sourceFile=this.createFile(path, "Main"+this.getExtension(compileRequest.getLanguage()));
            outputFile=this.createFile(path, "output.txt");
            inputFile=this.createFile(path,"input.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //writting code to Main.(ext) file
        try {
            this.writeDataToFile(sourceFile, compileRequest.getCode());
            if(compileRequest.getInput()!=null)
                this.writeDataToFile(inputFile, compileRequest.getInput());
        } catch (IOException e) {
            
            e.printStackTrace();
        }

        return this.compile(directory, inputFile, outputFile, compileRequest);

    }



    //compiles the code at os level
    private CompilationResponse compile(File directory,File inputFile,File outputFile,CompileRequest request){
        

        ProcessBuilder processBuilder=LANGUAGE_PROCESSBUILDERS.get(request.getLanguage()).get(0);
        CompilationResponse response=new CompilationResponse();

        InnerCompilationService compilationService=new InnerCompilationService(inputFile, outputFile, directory, processBuilder, request, response);
        try {
            response=executorService.submit(compilationService).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if(!request.getLanguage().equals("python") && response.getStatus().compareTo(Status.SUCCESS)==0){
            ExecutionService executionService=new ExecutionService(inputFile, outputFile, directory, LANGUAGE_PROCESSBUILDERS.get(request.getLanguage()).get(1), request, response);
            try {
                response=executorService.submit(executionService).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        response.setLanguage(request.getLanguage());
        response.setInput(request.getInput());
        return response;
    }



    //return the Extension of language python->.py, java->.java,c++->.cpp,c->.c
    private String getExtension(String language){
        return EXTENSION_MAP.get(language);
    }



    //returns the path for user_submissions
    private Path getPath(){
        String uuid=UUID.randomUUID().toString().replace("-", "");
        return Path.of(path+uuid);
    }


    //create a direcort
    private File createDirectory(Path path) throws IOException{
        File file=path.toFile();
        //creating directory
        if(file.mkdir()){
            return file; 
        }
        throw new IOException("Unable to create a directory");
    }


    //create a new file in given path with given name
    private File createFile(Path path,String name) throws IOException{
        Path filePath=path.resolve(name);
        File file=filePath.toFile();
        try {

            //modifying file permissions read and write done by owner only
            file.setReadable(true, true);
            file.setWritable(true,true);

            //file creation
            if(file.createNewFile()){
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }


    //Write data to file
    private boolean writeDataToFile(File file,String data) throws IOException{
        BufferedOutputStream outputStream=null;
        try{
            outputStream=new BufferedOutputStream(new FileOutputStream(file, false));
            outputStream.write(data.getBytes());
            outputStream.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
            throw new IOException("Unable to Write data to File", exception);
            
        }
        return true;
    }
}


class InnerCompilationService implements Callable<CompilationResponse>{

    private File inputFile, outputFile,directory;
    private ProcessBuilder processBuilder;
    private CompilationResponse response;

    //reentrantLock object
    private static final ReentrantLock reentrantLock;
    static{
        reentrantLock=new ReentrantLock(true);
    }




    public InnerCompilationService(File inputFile, File outputFile, File directory, ProcessBuilder processBuilder,CompileRequest request,CompilationResponse response) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.directory = directory;
        this.processBuilder = processBuilder;
        this.response=response;
    }




    @Override
    public CompilationResponse call() throws Exception {

        Process process=null;
        int exitCode=0;
        boolean timeOut=false;

        response.setStatus(Status.COMPILE_TIME_ERROR);

        //accuring lock
        if(reentrantLock.tryLock(reentrantLock.getQueueLength()*5,TimeUnit.SECONDS)){
            
            this.processBuilder.redirectInput(this.inputFile);
            this.processBuilder.redirectOutput(Redirect.to(this.outputFile));
            this.processBuilder.redirectError(Redirect.to(this.outputFile));

        
            
            //setting working directory
            this.processBuilder.directory(this.directory);

            try {
                process = processBuilder.start();
                timeOut=process.waitFor(5,TimeUnit.SECONDS);
                
                //destroying the process forcibly
                if(!timeOut)
                    process.destroyForcibly();
                exitCode=process.exitValue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            reentrantLock.unlock();
        }

        @SuppressWarnings("null")
        String time=process.info().totalCpuDuration().get().toString();
        

        //submission id and total run time in seconds
        response.setSubmissionId(directory.getName());
        response.setRunTime(time);


        //timeOut --> true if execution completed before 5 min
        //else false
        if(!timeOut){
            response.setStatus(Status.TIME_LIMIT_EXCEDDED);
            response.setErrorDetails("Taking time too much time then expected");
            return response;
        }

        else if(exitCode==1){
            response.setStatus(Status.COMPILE_TIME_ERROR);
            String errorDetails=this.readFileData(outputFile);
            response.setErrorDetails(errorDetails);
            return response;
        }
        
        String output=this.readFileData(outputFile);
        response.setOutput(output);
        response.setStatus(Status.SUCCESS);
        return response;
    }
    
    //Read the data from a given file
    private String readFileData(File file){
        BufferedInputStream inputStream=null;
        try{
            inputStream = new BufferedInputStream(new FileInputStream(file));
            String res=new String(inputStream.readAllBytes());
            inputStream.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occured while reading data";
        }
    }  
}



class ExecutionService implements Callable<CompilationResponse> {

    private File inputFile, outputFile,directory;
    private ProcessBuilder processBuilder;
    private CompileRequest request;
    private CompilationResponse response;

    //reentrantLock object
    private static final ReentrantLock reentrantLock;
    static{
        reentrantLock=new ReentrantLock(true);
    }

    public ExecutionService(File inputFile, File outputFile, File directory, ProcessBuilder processBuilder,CompileRequest request,CompilationResponse response) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.directory = directory;
        this.processBuilder = processBuilder;
        this.request = request;
        this.response=response;
    }

    @Override
    public CompilationResponse call() throws Exception{

        Process process=null;
        int exitCode=0;
        boolean timeOut=false;

        
        if(reentrantLock.tryLock(reentrantLock.getQueueLength()*5,TimeUnit.SECONDS)){
            if(this.request.getInput()!=null && !this.request.getInput().isBlank()){
                this.processBuilder.redirectInput(this.inputFile);
            }

            this.processBuilder.redirectOutput(this.outputFile);
            this.processBuilder.redirectError(this.outputFile);

        
            
            //setting working directory
            this.processBuilder.directory(this.directory);

            try {
                process = processBuilder.start();
                timeOut=process.waitFor(5,TimeUnit.SECONDS);
                
                //destroying process foreibly
                if(!timeOut)
                    process.destroyForcibly();
                exitCode=process.exitValue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            reentrantLock.unlock();

        }
        @SuppressWarnings("null")
        String time=process.info().totalCpuDuration().get().toString();
        

        //submission id and total run time in seconds
        response.setRunTime(time);


        //timeOut --> true if execution completed before 5 min
        //else false
        if(!timeOut){
            response.setStatus(Status.TIME_LIMIT_EXCEDDED);
            response.setErrorDetails("Taking time too much time then expected");
            return response;
        }

        else if(exitCode==1){
            response.setStatus(Status.RUN_TIME_ERROR);
            String errorDetails=this.readFileData(outputFile);
            response.setErrorDetails(errorDetails);
            return response;
        }
        
        String output=this.readFileData(outputFile);
        response.setOutput(output);
        response.setStatus(Status.SUCCESS);
        return response;
    }

    //Read the data from a given file
    private String readFileData(File file){
        BufferedInputStream inputStream=null;
        try{
            inputStream = new BufferedInputStream(new FileInputStream(file));
            String res=new String(inputStream.readAllBytes());
            inputStream.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occured while reading data";
        }
    }  

    
}
