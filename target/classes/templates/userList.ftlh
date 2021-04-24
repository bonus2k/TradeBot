<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="mb-1"><h5>Список пользователей</h5></div>

    <table class="table">
        <thead>
        <tr>
            <th scope="col">Логин</th>
            <th scope="col">Роль</th>
            <th scope="col">Торгует</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>
    <#list users as user>
        <tr>
            <th>${user.username}</th>
            <th><#list user.roles as role>${role}<#sep>, </#list></th>
            <th>${user.isRunString()}</th>
            <th><a href="/user/${user.id}">Edit</a></th>
        </tr>
    </#list>
        </tbody>
    </table>
</@c.page>