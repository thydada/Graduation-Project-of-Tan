<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', '微软雅黑', 'SimHei', '黑体', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  background-color: #ffffff;
  color: #333333;
  line-height: 1.4;
}

#app {
  min-height: 100vh;
  background-color: #ffffff;
}

/* 全局按钮样式重置 */
button {
  font-family: inherit;
}

/* 全局输入框样式重置 */
input, textarea, select {
  font-family: inherit;
}
</style>