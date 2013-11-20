<@repeat count=3; cnt>   
  nav.ftl
  ${cnt}. Test nav  
</@repeat> 
<@require name="widget/nav/nav2.js" />
<@script>
var nav=2;
a = a+1;
</@script>