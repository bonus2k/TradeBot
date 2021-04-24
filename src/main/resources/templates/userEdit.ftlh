<#import "parts/common.ftlh" as c>
<@c.page>


    <form action="/user" method="post">
        <div class="row">
            <div class="col-12 mb-3 mt-3"><h5>Редактирование пользователя</h5></div>

            <div class="col-6 mb-2">
                <label class="sr-only" for="inlineFormInputName">Name</label>
                <input type="text" class="form-control" id="inlineFormInputName" name="username" value=${user.username}>
            </div>

            <div class="row">
                <div class="col-md-3 mb-2 ml-2 mr-4">
                    <button type="button" class="btn btn-success">
                        Торгует
                        <span class="badge bg-success">
                            <input type="checkbox"
                                   name="isRun" ${user.isRun()?string("checked", "")}/>
                        </span>
                    </button>
                </div>

                    <#list roles as role>
                <div class="col-md-3 mb-2">
                        <button type="button" class="btn btn-primary">
                            ${role}
                            <span class="badge bg-primary">
                            <input type="checkbox"
                                   name=${role} ${user.roles?seq_contains(role)?string("checked", "")}/>
                        </span>
                        </button>
                </div>
                    </#list>


            </div>
            <div class="w-100"></div>
            <div class="col-10">
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

    <div class="mb-1"><h5>Список исполненных ордеров ${user.username}</h5></div>

    <div>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Курс</th>
                <th scope="col">Объем</th>
                <th scope="col">Операция</th>
                <th scope="col">Сумма</th>
                <th scope="col">Валюта</th>
                <th scope="col">Время</th>
            </tr>
            </thead>

            <tbody>
            <#list orders as order>
                <tr>

                    <td><b>${order.id}</b></td>
                    <td><span>${order.price}</span></td>
                    <td><span>${order.quantity?string["0.#######"]}</span></td>
                    <td><span>${order.side}</span></td>
                    <td><span>${order.sum}</span></td>
                    <td><span>${order.symbol}</span></td>
                    <td><b>${order.timestamp?string["MMM d, HH:mm"]}</b></td>

                </tr>
            <#else>
                No alerts
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>