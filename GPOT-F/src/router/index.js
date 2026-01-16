import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import WelcomeView from '../views/WelcomeView.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: WelcomeView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router