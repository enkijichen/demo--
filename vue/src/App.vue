<template>
  <router-view />
</template>

<style>
:root {
  --primary-color: #409eff;
  --dark-bg: #1a1a1a;
  --dark-secondary: #242424;
  --text-light: #e5eaf3;
  --text-gray: #8b949e;
  --border-color: #333;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  background-color: var(--dark-bg);
  color: var(--text-light);
  min-width: 1200px;  /* 设置最小宽度 */
  overflow-x: auto;   /* 允许横向滚动 */
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
