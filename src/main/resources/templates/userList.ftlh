<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="mb-1 mt-2 ml-3"><h5>Список пользователей</h5></div>
    <div class="col-md-10 mb-3 mt-2">
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Логин</th>
            <th scope="col">Роль</th>
            <th scope="col">Торгует</th>
            <th scope="col">Баланс</th>
            <th scope="col">Ордера</th>
            <th scope="col">Биллинг</th>
        </tr>
        </thead>
        <tbody>
    <#list users as user>
        <tr>
            <th><a href="/user/${user.id}">${user.username}</a></th>
            <th><#list user.roles as role>${role}<#sep>, </#list></th>
            <th>${user.isRunString()}</th>
            <th>${user.getAmount()?string["0.##"]}</th>
            <th><a href="/user/${user.id}/userOrder">Просмотр</a></th>
            <th><a href="/${user.id}/billing">Просмотр</a></th>
        </tr>
    </#list>
        </tbody>
    </table>
    </div>
</@c.page>