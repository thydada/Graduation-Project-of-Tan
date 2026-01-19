import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import SendPackageView from '../views/SendPackageView.vue'
import WelcomeView from '../views/WelcomeView.vue'
import PageA from '../views/PageA.vue'
import PageB from '../views/PageB.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/main',
    name: 'Main',
    component: MainLayout,
    children: [
      {
        path: 'send-package',
        name: 'SendPackage',
        component: SendPackageView
      }
    ]
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: WelcomeView
  },
  {
    path: '/page-a',
    name: 'PageA',
    component: PageA
  },
  {
    path: '/page-b',
    name: 'PageB',
    component: PageB
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router