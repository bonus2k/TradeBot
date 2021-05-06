<#import "parts/common.ftlh" as c>
<@c.page>


    <form action="/user" method="post">
<#--        <div class="row">-->
            <div class="col-12 mb-3 mt-3"><h5>Редактирование пользователя <b>${user.username}</b></h5></div>
            <div>
                <div class="col-md-7 mb-3 mt-2">
                    <table class="table table-sm">
                        <thead>
                        <tr>
                            <th scope="col">Торги</th>
                            <th scope="col">User</th>
                            <th scope="col">Admin</th>
                        </tr>
                        </thead>

                        <tbody>

                        <td><span><input type="checkbox" name="isRun" ${user.isRun()?string("checked", "")}/></span>
                        </td>
                        <#list roles as role>
                            <td><span><input type="checkbox"
                                             name=${role} ${user.roles?seq_contains(role)?string("checked", "")}/></span>
                            </td>
                        </#list>
                        </tbody>
                    </table>

                </div>
                <div class="col-md-7 mb-3 mt-2">
                    <label for="exampleFormControlTextarea1" class="form-label">Коментарии</label>
                    <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"
                              name="comment">${user.comment!""}</textarea>
                </div>

                <input type="hidden" name="_csrf" value="${_csrf.token}">
                <input type="hidden" name="userId" value=${user.id}>

                <div class="col-md-7 mb-3 mt-2">
                    <button type="submit" class="btn btn-primary">Сохранить</button>
                </div>
            </div>
    </form>
</@c.page>