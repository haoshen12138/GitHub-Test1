// jypt_web/src/router/index.js
// 路由配置
import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/Login.vue'
import Main from '../views/Main.vue'
import ProductDetail from '../views/ProductDetail.vue'
import SearchResult from '../views/SearchResult.vue'
import MyOrders from '../views/MyOrders.vue'
import TransactionDetail from '../views/TransactionDetail.vue'
import PersonalCenter from '../views/PersonalCenter.vue'
import Cart from '../views/Cart.vue'
import PublishProduct from '../views/PublishProduct.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/main',
    name: 'Main',
    component: Main
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/search',
    name: 'SearchResult',
    component: SearchResult
  },
  {
    path: '/orders',
    name: 'MyOrders',
    component: MyOrders
  },
  {
    path: '/transaction/:id',
    name: 'TransactionDetail',
    component: TransactionDetail
  },
  {
    path: '/profile',
    name: 'PersonalCenter',
    component: PersonalCenter
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart
  },
  {
    path: '/publish',
    name: 'PublishProduct',
    component: PublishProduct
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
