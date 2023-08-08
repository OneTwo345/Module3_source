package com.example.test_module.service;

import com.example.test_module.dao.TeacherDAO;
import com.example.test_module.model.Teacher;
import com.example.test_module.util.AppConstant;

import java.util.ArrayList;
import java.util.List;

public class TeacherService {
    private static List<Teacher> teachers;

    private static Integer currentId = 0;

    private static final TeacherService teacherService;

    private TeacherDAO teacherDAO = new TeacherDAO();

    static {
        teachers = new ArrayList<>();
//        users.add(new User(++currentId, "Vinh", "Vinh2","Vinh3","Vinh4", EGender.MALE,  Date.valueOf("1994-07-29"), "Demo"));
//        users.add(new User(++currentId, "Vinh", "Vinh2","Vinh3","Vinh4", EGender.MALE,  Date.valueOf("1994-07-29"), "Demo"));
//        users.add(new User(++currentId, "Vinh", "Vinh2","Vinh3","Vinh4", EGender.MALE,  Date.valueOf("1994-07-29"), "Demo"));
//        users.add(new User(++currentId, "Vinh", "Vinh2","Vinh3","Vinh4", EGender.MALE,  Date.valueOf("1994-07-29"), "Demo"));
        teacherService = new TeacherService();
    }

    public List<Teacher> getTeachers(PageableRequest request) {
        return teacherDAO.findAll(request);
    }

    public Teacher findById(Integer id) {
        return teacherDAO.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(AppConstant.ID_NOT_FOUND, "Teacher")));

//        return users.stream()
//                .filter(user -> Objects.equals(user.getId(), id)) // lọc qua list users với điều kiện là user id == id truyền vào
//                .findFirst() // lấy phần tử tìm thấy đầu tiên
//                .orElseThrow(() ->  new RuntimeException(String.format(AppConstant.ID_NOT_FOUND, "User")));
//            //nếu không tìm thấy thì trả ra lỗi
    }

    public void create(Teacher teacher) {
        teacherDAO.insertTeacher(teacher);
    }

    public static TeacherService getTeacherService() {
        return teacherService;
    }

    private TeacherService() {
    }

    public void edit(Teacher teacher) {
        teacherDAO.updateTeacher(teacher);
    }

    public boolean existById(Integer id) {
        return teacherDAO.findById(id).orElse(null) != null;
    }

    public void delete(Integer teacherId) {
        teacherDAO.deleteById(teacherId);
//        users = users
//                .stream()
//                .filter(user -> !Objects.equals(user.getId(), userId))
//                .collect(Collectors.toList());
    }
}
