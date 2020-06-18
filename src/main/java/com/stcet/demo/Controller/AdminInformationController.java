package com.stcet.demo.Controller;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;


//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.stcet.model.Subject;
import com.stcet.model.Teacher;
import com.stcet.spring.dao.SubjectsDAO;
import com.stcet.spring.dao.TeachersDAO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
//@RequestMapping("/testController")
public class AdminInformationController {
	
	@Autowired
	private SubjectsDAO obj;
	@Autowired
	private TeachersDAO tobj;
	@Autowired
	private ExcelGenerator gen;
	
	@RequestMapping(value = "/subjects", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public @ResponseBody List<Subject> getInfo()
	{
		for(Subject i:obj.getAllSubjects()) {
			System.out.println(i);
		}
		return obj.getAllSubjects();
	}
	
	
	@GetMapping("/getteacherbyid/{tid}")
	public Teacher getTeacherById(@PathVariable("tid") String tid)
	{
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		TeachersDAO fobj=context.getBean(TeachersDAO.class);
		context.close();*/
		return tobj.getTeacherByTid(tid);
	}
	
	@GetMapping("/getloadbyid/{wid}/{deptid}")
	public Subject getLoadById(@PathVariable("wid") String wid,@PathVariable("deptid") String deptid) {
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		WorkloadDAO obj=context.getBean(WorkloadDAO.class);
		context.close();*/
		return obj.getSubjectById(wid,deptid);
	}
	
	
	@RequestMapping(value = "/teachers", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public @ResponseBody List<Teacher> getTeachersInfo()
	{
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		TeachersDAO tobj=context.getBean(TeachersDAO.class);
		context.close();*/
		return tobj.getAllTeachers();
	}
	
	@PostMapping("/insertsubjectrecord")
	public Subject postSubjectRecord(@RequestBody Subject sub) {
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		SubjectsDAO obj=context.getBean(SubjectsDAO.class);
		context.close();*/
		obj.createSubject(sub);
		return sub;
	}
	
	//@PostMapping("/insertteacherrecord")
	@RequestMapping(value = "/teacher", 
            method = RequestMethod.POST,
            produces ="application/json")
	public @ResponseBody Teacher postTeacherRecord(@RequestBody Teacher teacher) {
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		TeachersDAO wobj=context.getBean(TeachersDAO.class);
		context.close();*/
		if(teacher.getId()==null)
			System.out.println("Post not happening!!!!");
		else
			tobj.createTeacher(teacher);
		return teacher;
	}
	
	@RequestMapping(value = "/deleteteacher/{id}", //
            method = RequestMethod.DELETE, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
	public void delTeacherRecord(@PathVariable("id") String id) {
		System.out.println("Deleting teacher with id= " + id);
		tobj.deleteTeacher(id);
	}
	
	@RequestMapping(value = "/subject/{subId}/{deptid}", //
            method = RequestMethod.DELETE, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public void deleteSubjectLoad(@PathVariable("subId") String subId,@PathVariable("deptid") String deptid) {
  
        System.out.println("(Service Side) Deleting subject with Id: " + subId);
        /*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		SubjectsDAO obj=context.getBean(SubjectsDAO.class);
		WorkloadDAO wobj=context.getBean(WorkloadDAO.class);
		context.close();*/
		obj.deleteSubject(subId,deptid);
    }
	
	@PutMapping("/updatesubjectrecord")
	public Subject updateSubjectRecord(@RequestBody Subject sub) {
		obj.updateSubject(sub);
		return sub;
	}
	
	//@PutMapping("/updateteacherrecord")
	@RequestMapping(value = "/teacher", //
            method = RequestMethod.PUT, //
            produces = { MediaType.APPLICATION_JSON_VALUE, //
                    MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
	public Teacher updateTeacherRecord(@RequestBody Teacher teacher) {
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		TeachersDAO obj=context.getBean(TeachersDAO.class);
		context.close();*/
		tobj.updateTeacher(teacher);
		return teacher;
	}
	
	
	
	@RequestMapping(value = "/subject", 
            method = RequestMethod.POST, 
            produces ="application/json")
    public @ResponseBody Subject addSubjectLoad(@RequestBody Subject subjectForm) {
  
        System.out.println("(Service Side) Creating subject with Subject Id: " + subjectForm.getId());
        Subject sub=new Subject();
        sub.setId(subjectForm.getId());
        System.out.println(subjectForm.getPractical()+"  aaaaaaa");
        sub.setPractical(subjectForm.getPractical());
        sub.setLength(subjectForm.getLength());
        sub.setHours(subjectForm.getHours());
        sub.setN_teachers(subjectForm.getN_teachers());
        sub.setDept(subjectForm.getDept());
        sub.setLab(subjectForm.getLab());
        sub.setTeachersList(subjectForm.getTeachersList());
        try
        {
        	/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
        	SubjectsDAO obj=context.getBean(SubjectsDAO.class);
        	context.close();
        	AnnotationConfigApplicationContext context2=new AnnotationConfigApplicationContext(AppConfig.class); 
        	WorkloadDAO wobj=context2.getBean(WorkloadDAO.class);
        	context2.close();*/
        	obj.createSubject(sub);
        }
        catch(IllegalStateException e) {
        	System.out.println("Illegal state");
        }
        return subjectForm;
    }
  
    // URL:
    // http://localhost:8080/SomeContextPath/employee
    // http://localhost:8080/SomeContextPath/employee.xml
    // http://localhost:8080/SomeContextPath/employee.json
	@RequestMapping(value = "/subject", 
            method = RequestMethod.PUT, 
            produces ="application/json")
    public @ResponseBody Subject updateSubjectLoad(@RequestBody Subject subjectForm) {
  
        System.out.println("(Service Side) Editing subject with Id: " + subjectForm.getId());
        Subject sub=new Subject();
        sub.setId(subjectForm.getId());
        sub.setPractical(subjectForm.getPractical());
        sub.setLength(subjectForm.getLength());
        sub.setHours(subjectForm.getHours());
        sub.setN_teachers(subjectForm.getN_teachers());
        sub.setDept(subjectForm.getDept());
        sub.setLab(subjectForm.getLab());
        sub.setTeachersList(subjectForm.getTeachersList());
        /*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		SubjectsDAO obj=context.getBean(SubjectsDAO.class);
		WorkloadDAO wobj=context.getBean(WorkloadDAO.class);
		context.close();*/
		obj.updateSubject(sub);
        return subjectForm;
    }
	
	//Link for generating sheets
    @RequestMapping(value = "/generate")
    public ModelAndView generateSheets()throws Exception {
        gen.startGeneration();
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("generate");
        // return IOUtils.toByteArray(in);
        return modelAndView;
    }
	
    
    @GetMapping(value="/subjectsfirst",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadFirst() throws IOException{

    		try{
    			String path = "src/main/resources/public/FirstYear.xlsx"; //path of your file
    		    return new FileSystemResource(new File(path));
    		}
    		catch(Exception e){System.out.println("error in file_download "+e); 
    		return null;
    		}
    }
    
    @GetMapping(value="/subjectssecond",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadSecond() throws IOException{
    	try{
    		String path = "src/main/resources/public/SecondYear.xlsx"; //path of your file
    	    return new FileSystemResource(new File(path));
    	}
    	catch(Exception e){System.out.println("error in file_download "+e); 
      		return null;
    	}
    }
    
    @GetMapping(value="/subjectsthird",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadThird() throws IOException{
    	try{
    		String path = "src/main/resources/public/ThirdYear.xlsx"; //path of your file
    	    return new FileSystemResource(new File(path));
    	}
    	catch(Exception e){System.out.println("error in file_download "+e); 
    		return null;
    	}
    }
    

    @GetMapping(value="/subjectsfourth",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadFourth() throws IOException{
    	try{
    		String path = "src/main/resources/public/FourthYear.xlsx"; //path of your file
    	    return new FileSystemResource(new File(path));
    	}
    	catch(Exception e){System.out.println("error in file_download "+e); 
    		return null;
    	}
    }
    
    //<button onclick="location.href='/subjects.xlsx'" class="button_1">Download</button>
    /*@GetMapping(value = "/subjects.xlsxfirst")
    public ResponseEntity<InputStreamResource> downloadSubjects1()throws IOException {
    	int year=1;
        ByteArrayInputStream in = gen.startGeneration(year);
        // return IOUtils.toByteArray(in);
        
        HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=subjects.xlsx");
        
         return ResponseEntity
                      .ok()
                      .headers(headers)
                      .body(new InputStreamResource(in));
    	
    }
    @GetMapping(value = "/subjects.xlsxsecond")
    public ResponseEntity<InputStreamResource> downloadSubjects2()throws IOException {
    	int year=2;
        ByteArrayInputStream in = gen.startGeneration(year);
        // return IOUtils.toByteArray(in);
        
        HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=subjects.xlsx");
        
         return ResponseEntity
                      .ok()
                      .headers(headers)
                      .body(new InputStreamResource(in));
    	
    }
    @GetMapping(value = "/subjects.xlsxthird")
    public ResponseEntity<InputStreamResource> downloadSubjects3()throws IOException {
    	int year=3;
        ByteArrayInputStream in = gen.startGeneration(year);
        // return IOUtils.toByteArray(in);
        
        HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=subjects.xlsx");
        
         return ResponseEntity
                      .ok()
                      .headers(headers)
                      .body(new InputStreamResource(in));
    	
    }
    
    @GetMapping(value = "/subjects.xlsxfourth")
    public ResponseEntity<InputStreamResource> downloadSubjects4()throws IOException {
    	int year=4;
        ByteArrayInputStream in = gen.startGeneration(year);
        // return IOUtils.toByteArray(in);
        
        HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=subjects.xlsx");
        
         return ResponseEntity
                      .ok()
                      .headers(headers)
                      .body(new InputStreamResource(in));
    	
    }*/
}
