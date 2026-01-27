import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import SendPackageView from '../views/SendPackageView.vue'
import WelcomeView from '../views/WelcomeView.vue'
import PageA from '../views/PageA.vue'
import PageB from '../views/PageB.vue'
import ExceptionView from '../views/ExceptionView.vue'
import EmployeeLayout from '../views/EmployeeLayout.vue'
import EmployeeProfile from '../views/EmployeeProfile.vue'
import TrackPackageView from '../views/TrackPackageView.vue'

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
      },
      {
        path: 'track-package',
        name: 'TrackPackage',
        component: TrackPackageView
      }
    ]
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: WelcomeView
  },
  // 员工系统布局
  {
    path: '/employee',
    name: 'Employee',
    component: EmployeeLayout,
    children: [
      {
        path: 'profile',
        name: 'EmployeeProfile',
        component: EmployeeProfile
      },
      {
        path: 'pickup',
        name: 'EmployeePickup',
        component: PageA
      },
      {
        path: 'exception',
        name: 'EmployeeException',
        component: ExceptionView
      }
    ]
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
  },
  {
    path: '/exception',
    name: 'ExceptionStandalone',
    component: ExceptionView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
