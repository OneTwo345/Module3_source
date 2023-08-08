<%--
  Created by IntelliJ IDEA.
  User: ADMINIS
  Date: 8/7/2023
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.example.test_module.model.Teacher" %><%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 7/27/2023
  Time: 3:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
    <link href="../style.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.0/jquery.min.js" integrity="sha512-3gJwYpMe3QewGELv8k/BX9vcqhryRdzRMxVfq6ngyWXwo03GFEzjsUm8Q7RZcHPHksttq7/GFoxjCVUjkjvPdw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js" integrity="sha512-VEd+nq25CkR676O+pLBnDW09R7VQX9Mdiij052gVCp5yVH3jGtH70Ho/UUv4mJDsEdTvqRCFZg0NKGiojGnUCw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.css" integrity="sha512-3pIirOrwegjM6erE5gPSwkUzO+3cTjpnV9lexlNZqvupR64iZBnOOTiiLPb9M36zpMScbmUNIcHUqKD47M719g==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<h1>Create</h1>
<h3>${message}</h3>
<form method="post"
        <c:if test = "${teacher.id == null}"> action="/teachers?action=create" </c:if>
        <c:if test = "${teacher.id != null}"> action="/teachers?action=edit" </c:if>
>
    <%--        cần phải có thằng formBody thì xài được validation js--%>
    <div id="formBody" class="row">

    </div>
    <button class="btn btn-primary" type="submit">
        <c:if test = "${teacher.id != null}"> Edit Teacher </c:if>
        <c:if test = "${teacher.id == null}"> Create Teacher </c:if>
    </button>
    <a href="/teachers" class="btn btn-secondary" onclick="console.log(${teacher.toString()})">Back</a>
</form>
<script src="../base.js" ></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
<%--    <% var user = (User) request.getAttribute("user"); %>--%>
<script>
    <%--const user =<%= user%>;--%>
    //lấy được userở đây mấy ae tra google để
    // lấy data user từ controller
    var teacher = ${teacherJSON};
    const genders = ${genderJSON};
    const categories = ${categoriesJSON};
    const inputs = [
        {
            label: "Name",
            name: "name",
            pattern: "^[A-Za-z ]{6,20}",
            message: "Name must have minimun is 6 charaters and maximun is 20 charaters",
            require: true,
            classDiv: 'col-6',
            value: teacher.name || ''
        },
        {
            name:'id',
            value: teacher.id,
            type: 'hidden',
            classDiv: 'd-none'
        },

        {
            label: "Date of Birth",
            name: "dob",
            type: "date",
            require: true,
            value:  teacher.dob || '',
            classDiv: 'col-6'
        },
        {
            label: "Hobby",
            name: "hobby",
            pattern: "^[A-Za-z ]{10,20}",
            message: "Hobby must have minimun is 10 charaters and maximun is 20 charaters",
            require: true,
            classDiv: 'col-6',
            value: teacher.hobby || ''
        },
        {
            label: "Gender",
            name: "gender",
            type: "select",
            message: "Please choose gender",
            options: genders?.map(e=> {
                return {
                    name: e,
                    value: e
                }
            }),
            require: true,
            value: teacher.gender || '',
            classDiv: 'col-6'
        },
        {
            label: "Category",
            name: "category_id",
            type: "select",
            message: "Please choose category",
            options: categories?.map(e=> {
                return {
                    name: e.name,
                    value: e.id
                }
            }),
            require: true,
            value: teacher.category?.id || '', // có nghĩa là  nếu user.role có giá trị thì sẽ lấy id của role còn không thì lấy ''
            classDiv: 'col-6'
        }
    ];
    // phải có những dòng dưới này

    const formBody = document.getElementById('formBody'); // DOM formBody theo id

    // loop qua inputs
    inputs.forEach((props, index) => {
        // vẽ từng ô input
        formBody.innerHTML += formInput(props, index);
    })
</script>

</body>
</html>