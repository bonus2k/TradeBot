<#import "parts/common.ftlh" as c>
<#import "parts/order.ftlh" as o>
<@c.page>
    <div class="mb-1"><h5>Список исполненных ордеров <b>${user.username}</b></h5></div>
   <@o.order url/>


</@c.page>