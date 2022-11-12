<#--我是一个freemarker注释-->
<h1 style="color: red">Hello
    <#-- ??判断某个变量是否存在，如果存则返回true -->
    <#if name1??>
        ${name1}
    </#if>
   </h1><br/>
<#list students as item>
<#-- if判断 -->
    <#if item.age lt 20>
        <span style="color: red">${item_index*2}. ${item.name}——${item.birthday?string('yyyy年MM月dd日')}—${item.age} 资产：${item.money}</span><br/>
    <#else>
        ${item_index*2}. ${item.name}—${item.age} 资产：${item.money}<br/>
    </#if>
</#list>