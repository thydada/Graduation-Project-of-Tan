import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import UserPackagesView from '../views/UserPackagesView.vue'
import UserMessagesView from '../views/UserMessagesView.vue'
import WelcomeView from '../views/WelcomeView.vue'
import AdminDashboardView from '../views/AdminDashboardView.vue'
import ExceptionView from '../views/ExceptionView.vue'
import EmployeeLayout from '../views/EmployeeLayout.vue'
import EmployeeProfileView from '../views/EmployeeProfileView.vue'
import EmployeePackageView from '../views/EmployeePackageView.vue'
import EmployeeExceptionView from '../views/EmployeeExceptionView.vue'
import EmployeeAllPackagesView from '../views/EmployeeAllPackagesView.vue'
import EmployeeOutboundView from '../views/EmployeeOutboundView.vue'
import EmployeeDeliveryView from '../views/EmployeeDeliveryView.vue'
import EmployeeSendMessageView from '../views/EmployeeSendMessageView.vue'
import DebugCreatePackageView from '../views/DebugCreatePackageView.vue'
import TerminalQueryView from '../views/TerminalQueryView.vue'
import OutboundMachineView from '../views/OutboundMachineView.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/debug',
    name: 'DebugCreatePackage',
    component: DebugCreatePackageView
  },
  {
    path: '/terminal',
    name: 'TerminalQuery',
    component: TerminalQueryView
  },
  {
    path: '/outbound-machine',
    name: 'OutboundMachine',
    component: OutboundMachineView
  },
  {
    path: '/main',
    name: 'Main',
    component: MainLayout,
    redirect: '/main/packages',
    children: [
      {
        path: 'packages',
        name: 'UserPackages',
        component: UserPackagesView
      },
      {
        path: 'messages',
        name: 'UserMessages',
        component: UserMessagesView
      }
    ]
  },
  {
    path: '/welcome',
    name: 'Welcome',
    component: WelcomeView
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboardView
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
        path: 'package/scan',
        name: 'EmployeePackageScan',
        component: EmployeePackageView
      },
      {
        path: 'package/manual',
        name: 'EmployeePackageManual',
        component: EmployeePackageView
      },
      {
        path: 'package/exception',
        name: 'EmployeePackageException',
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
      },
      {
        path: 'send-message',
        name: 'EmployeeSendMessage',
        component: EmployeeSendMessageView
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router