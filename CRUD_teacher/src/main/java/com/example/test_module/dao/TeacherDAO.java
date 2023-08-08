package com.example.test_module.dao;

import com.example.test_module.model.Category;
import com.example.test_module.model.Teacher;
import com.example.test_module.model.enums.EGender;
import com.example.test_module.service.PageableRequest;
import com.example.test_module.service.dto.enums.ESortType;
import com.example.test_module.util.AppUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAO extends DatabaseConnection {

    private final String SELECT_ALL_TEACHERS = "SELECT t.*,c.name `category.name`, c.id as `category.id`  FROM `Teachers` t LEFT JOIN " +
            "`categories` c on t.category_id = c.id  WHERE t.`name` like '%s' OR t.`hobby` LIKE '%s' OR t.`gender` LIKE '%s' Order BY %s %s LIMIT %s OFFSET %s";
    private final String SELECT_TOTAL_RECORDS = "SELECT COUNT(1) as cnt  FROM `teachers` t LEFT JOIN " +
            "`categories` c on t.category_id = c.id  WHERE t.`name` like '%s' OR t.`hobby` LIKE '%s'";
//    private final String UPDATE_TEACHER = "UPDATE `teachers` SET `name` = ?, `dob` = ?, `hobby` = ?, `gender` = ?, `category_id` = ? WHERE (`id` = ?);";
//
//    private final String INSERT_TEACHER = "INSERT INTO `teachers` (`name`, `dob`, `hobby`, `gender`, `category_id`) \n" +
//            "VALUES (?, ?, ?, ?, ?)";
    private final String FIND_BY_ID = "SELECT t.*,c.name category_name  FROM " +
            "`teachers` t LEFT JOIN `categories` c on t.category_id = c.id WHERE t.`id` = ?"; // show Edit

    private final String DELETE_BY_ID = "DELETE FROM `teachers` WHERE (`id` = ?)";


    public List<Teacher> findAll(PageableRequest request) {
        List<Teacher> teachers = new ArrayList<>();
        String search = request.getSearch();
        if(request.getSortField() == null){
            request.setSortField("id");
        }
        if(request.getSortType() == null){
            request.setSortType(ESortType.DESC);
        }
        if(search == null){
            search = "%%";
        }else {
            search = "%" + search + "%";
        }
        var offset = (request.getPage() - 1) * request.getLimit();
        try (Connection connection = getConnection();

             // Step 2: truyền câu lênh mình muốn chạy nằm ở trong này (SELECT_USERS)
             PreparedStatement preparedStatement = connection
                     .prepareStatement(String.format(SELECT_ALL_TEACHERS, search, search,search,
                             request.getSortField(), request.getSortType(), request.getLimit(), offset))) {

            System.out.println(preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();
            //

            while (rs.next()) {
                teachers.add(AppUtil.getObjectFromResultSet(rs, Teacher.class));
            }
            PreparedStatement statementTotalRecords = connection
                    .prepareStatement(String.format(SELECT_TOTAL_RECORDS, search, search));
            ResultSet resultSetOfTotalRecords = statementTotalRecords.executeQuery();
            if(resultSetOfTotalRecords.next()){
                int totalPage =
                        (int) Math.ceil(resultSetOfTotalRecords.getDouble("cnt")/request.getLimit());
                request.setTotalPage(totalPage);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teachers;
    }

    public void insertTeacher(Teacher teacher){
        try (Connection connection = getConnection();

             // Step 2: truyền câu lênh mình muốn chạy nằm ở trong này (SELECT_USERS)
             PreparedStatement preparedStatement = connection
                     .prepareStatement(AppUtil.buildInsertSql("teachers",teacher))) {
            System.out.println(preparedStatement);
//            preparedStatement.setString(1,teacher.getName());
//            preparedStatement.setString(2,teacher.getDob());
//            preparedStatement.setString(3,teacher.getHobby());
//            preparedStatement.setString(4,teacher.getGender().toString());
//            preparedStatement.setInt(5,teacher.getCategory().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTeacher(Teacher teacher) {
        try (Connection connection = getConnection();

             // Step 2: truyền câu lênh mình muốn chạy nằm ở trong này (SELECT_USERS)
             PreparedStatement preparedStatement = connection
                     .prepareStatement(AppUtil.buildUpdateSql("teachers",teacher))) {
            System.out.println(preparedStatement);
//            preparedStatement.setString(1,teacher.getName());
//            preparedStatement.setString(2,teacher.getDob());
//            preparedStatement.setString(3,teacher.getHobby());
//            preparedStatement.setString(4,teacher.getGender().toString());
//            preparedStatement.setInt(5,teacher.getCategory().getId());
//            preparedStatement.setLong(6,teacher.getId());


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Teacher> findById(Integer id) {
        try (Connection connection = getConnection();

             // Step 2: truyền câu lênh mình muốn chạy nằm ở trong này (SELECT_USERS)
             PreparedStatement preparedStatement = connection
                     .prepareStatement(FIND_BY_ID)) {
            preparedStatement.setLong(1, id);
            System.out.println(preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(AppUtil.getObjectFromResultSet(rs, Teacher.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void deleteById(Integer id) {
        try (Connection connection = getConnection();

             // Step 2: truyền câu lênh mình muốn chạy nằm ở trong này (SELECT_USERS)
             PreparedStatement preparedStatement = connection
                     .prepareStatement(DELETE_BY_ID)) {
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Teacher getTeacherByResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        Date dob = rs.getDate("dob");
        String hobby = rs.getString("hobby");
        String gender = rs.getString("gender");
        String categoryName = rs.getString("category_name");
        Integer categoryId = rs.getInt("category_id");
        Category category = new Category(categoryId, categoryName);
        return new Teacher(id, name,dob,hobby, EGender.valueOf(gender),category);
    }
}
