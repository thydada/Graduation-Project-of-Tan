import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import SendPackageView from '../views/SendPackageView.vue'
import UserPackagesView from '../views/UserPackagesView.vue'
import WelcomeView from '../views/WelcomeView.vue'
import PageA from '../views/PageA.vue'
import PageB from '../views/PageB.vue'
import ExceptionView from '../views/ExceptionView.vue'
import EmployeeLayout from '../views/EmployeeLayout.vue'
import EmployeeProfileView from '../views/EmployeeProfileView.vue'
import EmployeePackageView from '../views/EmployeePackageView.vue'
import EmployeeExceptionView from '../views/EmployeeExceptionView.vue'
import EmployeeAllPackagesView from '../views/EmployeeAllPackagesView.vue'
import EmployeeOutboundView from '../views/EmployeeOutboundView.vue'
import EmployeeDeliveryView from '../views/EmployeeDeliveryView.vue'

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
    redirect: '/main/send-package',
    children: [
      {
        path: 'send-package',
        name: 'SendPackage',
        component: SendPackageView
      },
      {
        path: 'packages',
        name: 'UserPackages',
        component: UserPackagesView
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
  },
  {
    path: '/exception',
    name: 'ExceptionStandalone',
    component: ExceptionView
  },
  {
    path: '/employee',
    name: 'Employee',
    component: EmployeeLayout,
    redirect: '/employee/profile',
    children: [
      {
        path: 'profile',
        name: 'EmployeeProfile',
        component: EmployeeProfileView
      },
      {
        path: 'package',
        name: 'EmployeePackage',
        component: EmployeePackageView
      },
      {
        path: 'exception',
        name: 'EmployeeException',
        component: EmployeeExceptionView
      },
      {
        path: 'all-packages',
        name: 'EmployeeAllPackages',
        component: EmployeeAllPackagesView
      },
      {
        path: 'outbound',
        name: 'EmployeeOutbound',
        component: EmployeeOutboundView
      },
      {
        path: 'delivery',
        name: 'EmployeeDelivery',
        component: EmployeeDeliveryView
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router