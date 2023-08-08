package com.example.test_module.controller;

import com.example.test_module.model.enums.EGender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.test_module.model.Teacher;
import com.example.test_module.service.CategoryService;
import com.example.test_module.service.PageableRequest;
import com.example.test_module.service.TeacherService;
import com.example.test_module.service.dto.enums.ESortType;
import com.example.test_module.util.AppConstant;
import com.example.test_module.util.AppUtil;
import com.example.test_module.util.RunnableCustom;
import com.example.test_module.util.RunnableWithRegex;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(urlPatterns = "/teachers", name = "TeacherController")
public class TeacherController extends HttpServlet {
    private final String PAGE = "teachers"; // đặt hằng số

    private Map<String, RunnableCustom> validators;

    private final Map<String, String> errors = new HashMap<>(); // tạo map để validators add lỗi vào map này

    @Override
    public void init() {
        validators = new HashMap<>();
        // tạo validator với name field là phone, và nó validate theo Regex Pattern
        // tạo tất các validator cho all fields.
        // mình có thế xài cái thằng khác
        validators.put("name", new RunnableWithRegex("^[A-Za-z ]{6,20}", "name", errors));
        validators.put("hobby", new RunnableWithRegex("^[A-Za-z ]{10,20}", "hobby", errors));
        validators.put("gender", new RunnableWithRegex("^(Nam|Nu)$", "gender", errors));
        validators.put("category_id", new RunnableWithRegex( "^(1|2)$", "category", errors));
//        validators.put("dob", new RunnableWithRegex("\\\\d{2}/\\\\d{2}/\\\\d{4}", "dob", errors));
        //định nghĩa tất cả các fields
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter(AppConstant.ACTION);

//        if(Objects.equals(action, AppConstant.EDIT)){
//            showEdit(req,resp);
//            return;
//        }
//        if (Objects.equals(action, AppConstant.CREATE)) {
//            showCreate(req, resp);
//            return;
//        }
        if (Objects.equals(action, AppConstant.DELETE)) {
            delete(req, resp);
            return;
        }

        showList(req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        errors.clear(); // clear lỗi cũ
        String action = req.getParameter(AppConstant.ACTION); // lấy action
        if (Objects.equals(action, AppConstant.CREATE)) {
            //kiểm tra xem action = create thi call create
            create(req, resp);
            return;
        }
        if (Objects.equals(action, AppConstant.EDIT)) {
            //kiểm tra xem action = create thi call edit
            edit(req, resp);
            return;
        }

        showList(req, resp);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Teacher teacher = getValidTeacher(req,resp); // lấy ra user và + xử lý cho việc validation của các field trong class User.
        if(errors.size() == 0){ //không xảy lỗi (errors size == 0) thì mình mới tạo user.
            TeacherService.getTeacherService().create(teacher);
            resp.sendRedirect("/teachers?message=Created");
        }

    }
    private void edit(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Teacher teacher = getValidTeacher(req,resp); // lấy ra user và + xử lý cho việc validation của các field trong class User.
        if(errors.size() == 0){ //không xảy lỗi (errors size == 0) thì mình mới sửa user.
            TeacherService.getTeacherService().edit(teacher);
            resp.sendRedirect("/teachers?message=Edited");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //page, Integer totalOfPage, Integer limit, Integer totalPage
        PageableRequest request = new PageableRequest(
                req.getParameter("search"),
                req.getParameter("sortField"),
                ESortType.valueOf(AppUtil.getParameterWithDefaultValue(req,"sortType", ESortType.DESC).toString()),
                Integer.parseInt(AppUtil.getParameterWithDefaultValue(req, "page", "1").toString()),
                Integer.parseInt(AppUtil.getParameterWithDefaultValue(req, "limit", "5").toString())
        ); //tao doi tuong pageable voi parametter search

        req.setAttribute("pageable", request);
        req.setAttribute("teachers", TeacherService.getTeacherService().getTeachers(request)); // gửi qua list users để jsp vẻ lên trang web
        req.setAttribute("teachersJSON", new ObjectMapper().writeValueAsString(TeacherService.getTeacherService().getTeachers(request)));
        req.setAttribute("message", req.getParameter("message")); // gửi qua message để toastr show thông báo
        req.setAttribute("genderJSON", new ObjectMapper().writeValueAsString(EGender.values()));
        req.setAttribute("categoriesJSON", new ObjectMapper().writeValueAsString(CategoryService.getCategories()));
        req.getRequestDispatcher(PAGE + AppConstant.LIST_PAGE).forward(req,resp);
    }

//    private void showCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setAttribute("teachersJSON", new ObjectMapper().writeValueAsString(new Teacher())); // gửi qua user rỗng để JS vẻ lên trang web
//        req.setAttribute("genderJSON", new ObjectMapper().writeValueAsString(EGender.values()));
//        req.setAttribute("categoriesJSON", new ObjectMapper().writeValueAsString(CategoryService.getCategories()));
//        req.getRequestDispatcher(PAGE + AppConstant.CREATE_PAGE)
//                .forward(req,resp);
//    }
//    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        Integer id = Integer.valueOf(req.getParameter("id"));
//        if(checkIdNotFound(req, resp, id)) return;
////        req.setAttribute("genderJSON", new ObjectMapper().writeValueAsString(EGender.values()));
//        req.setAttribute("teachers", TeacherService.getTeacherService().findById(id)); // gửi user để jsp check xem edit hay là create User
//        req.setAttribute("teachersJSON", new ObjectMapper().writeValueAsString(TeacherService.getTeacherService().findById(id))); //  // gửi qua user được tìm thấy bằng id để JS vẻ lên trang web
//        req.setAttribute("categoriesJSON", new ObjectMapper().writeValueAsString(CategoryService.getCategories()));
//        req.getRequestDispatcher(PAGE + AppConstant.CREATE_PAGE)
//                .forward(req,resp);
//
//    }
    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = Integer.valueOf(req.getParameter("id"));
        if(checkIdNotFound(req, resp, id)) return;
        TeacherService.getTeacherService().delete(id);
        resp.sendRedirect(PAGE + "?message=Deleted");
    }

    private Teacher getValidTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Teacher teacher = (Teacher) AppUtil.getObjectWithValidation(req, Teacher.class,  validators); //
        if(errors.size() > 0){
            req.setAttribute("teachersJSON", new ObjectMapper().writeValueAsString(teacher)); //hiểu dòng đơn giản là muốn gửi data qua JS thì phải xài thằng này  new ObjectMapper().writeValueAsString(user).
            req.setAttribute("categoriesJSON", new ObjectMapper().writeValueAsString(CategoryService.getCategories()));
            req.setAttribute("genderJSON", new ObjectMapper().writeValueAsString(EGender.values()));
            req.setAttribute("message","Something was wrong");
            req.getRequestDispatcher(PAGE + AppConstant.CREATE_PAGE)
                    .forward(req,resp);
        }
        return teacher;
    }

    private boolean checkIdNotFound(HttpServletRequest req, HttpServletResponse resp, Integer id) throws IOException{
        if(!TeacherService.getTeacherService().existById(id)){
            resp.sendRedirect(PAGE + "?message=Id not found");
            return true;
        }
        return false;
    }
}
