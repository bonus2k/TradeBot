<#include "parts/security.ftlh">
<#import "parts/common.ftlh" as c>
<@c.page>
  <h5>Привет,
    <#if name !="Гость">
      ${name}!
    <#else>
      Гость!
    </#if></h5>
    <div>Petite, et dabitur vobis; quaerite et invenietis; pulsate, et aperietur vobis</div>
</@c.page>