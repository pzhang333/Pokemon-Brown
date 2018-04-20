<#assign content>

<h1> Pokemon </h1>

<body>
<div id="output">
    ${readmessage} <br>
    ${message}
</div>
</body>
</#assign>

<#include "main.ftl">

<script>
$(document).ready(function() {
  setup_player_connection();
});
</script>