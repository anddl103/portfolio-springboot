// Imports
import { kebabCase } from 'lodash'
import { leadingSlash, trailingSlash } from '@/util/helpers'
import { routeName } from '@/router'
import { role } from '@/assets/enums'
import store from '@/store'

export function abort (code = 404) {
  return {
    name: 'FourOhFour',
    path: '*',
    component: () => error(code),
  }
}

export function error (code = 404) {
  return import(
    /* webpackChunkName: "error-[request]" */
    `@/views/${code}.vue`
  )
}

export function layout (layout = 'Default', children, path = '') {
  const dir = kebabCase(layout)

  return {
    children,
    component: () => import(
      /* webpackChunkName: "layout-[request]" */
      `@/layouts/${dir}/Index`
    ),
    path,
  }
}

export function redirect (
  path = '*',
  rhandler,
) {
  if (typeof path === 'function') {
    rhandler = path
    path = '*'
  }

  return {
    path,
    redirect: to => {
      const rpath = rhandler(to)
      const url = rpath !== ''
        ? leadingSlash(trailingSlash(rpath))
        : rpath

      return `/${url}`
    },
  }
}

export function route (name, component, path = '', authRequired = true) {
  component = Object(component) === component
    ? component
    : { default: name.replace(' ', '') }

  const components = {}

  for (const [key, value] of Object.entries(component)) {
    components[key] = () => import(
      /* webpackChunkName: "views-[request]" */
      `@/views/${value}`
    )
  }
  return {
    name,
    components,
    path,
    meta: { requiresAuth: authRequired },
    beforeEnter: (to, from, next) => {
      const currentRole = store.state.auth.currentRole
      function setGroupAndGetNext (...routeName) {
        const group = []
        group.push(...routeName)
        if (group.includes(currentRole)) {
          next()
        } else {
          next(from.path)
        }
      }
      switch (to.name) {
        case routeName.DASHBOARD:
        case routeName.MY_INFO:
          setGroupAndGetNext(role.SUPER_ADMIN, role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.CS_MANAGER, role.VIEWER)
          break
        case routeName.ADMIN:
          setGroupAndGetNext(role.SUPER_ADMIN, role.VIEWER)
          break
        case routeName.ARTIST:
        case routeName.ARTIST_FORM:
        case routeName.ALBUM_DEPLOYED:
        case routeName.ALBUM_DEPLOYED_DETAIL:
        case routeName.COUNTRY_CODE:
          setGroupAndGetNext(role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.VIEWER)
          break
        case routeName.ALBUM:
        case routeName.ALBUM_DETAIL:
        case routeName.BANNER:
        case routeName.BANNER_FORM:
        case routeName.BATCH_STATE:
          setGroupAndGetNext(role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.VIEWER)
          break
        case routeName.NFC:
        case routeName.NFC_FORM:
        case routeName.ORDER:
        case routeName.ORDER_FORM:
          setGroupAndGetNext(role.PRODUCT_MANAGER, role.VIEWER)
          break
        case routeName.FAQ:
        case routeName.FAQ_FORM:
        case routeName.FAQ_CATEGORY:
        case routeName.NOTICE:
        case routeName.NOTICE_FORM:
        case routeName.QUESTION:
        case routeName.QUESTION_FORM:
          setGroupAndGetNext(role.CS_MANAGER, role.VIEWER)
          break
        case routeName.USER:
        case routeName.USER_FORM:
          setGroupAndGetNext(role.CS_MANAGER)
          break
        case routeName.LANGUAGE:
          setGroupAndGetNext(role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.CS_MANAGER, role.VIEWER)
          break
        case routeName.DEMO_TOOL:
          setGroupAndGetNext(role.PRODUCT_MANAGER)
          break
        default:
          next()
      }
    },
  }
}
