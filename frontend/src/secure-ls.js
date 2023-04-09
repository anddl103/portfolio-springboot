import SecureLS from 'secure-ls'

const ls = new SecureLS({ encodingType: 'rc4', isCompression: false })

export function clearLocalStorage () {
  ls.remove('vuex')
}

export const instance = ls
