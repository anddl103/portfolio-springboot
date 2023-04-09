export const operationType = Object.freeze({
  INSERT: 'INSERT',
  UPDATE: 'UPDATE',
  DELETE: 'DELETE',
  NONE: 'NONE',
})

export const contentsType = Object.freeze({
  STILL: 'STILL',
  LIVE: 'LIVE',
  VIDEO: 'VIDEO',
})

export const nfcCardStatus = Object.freeze({
  BEFORE_ORDER: 'BEFORE_ORDER',
  AFTER_ORDER: 'AFTER_ORDER',
})

export const productOrderStatus = Object.freeze({
  CREATED: 'CREATED',
  COMPLETE: 'COMPLETE',
  CANCELLED: 'CANCELLED',
})

export const serviceRegion = Object.freeze({
  GLOBAL: 'GLOBAL',
  CHINA: 'CHINA',
})

export const usage = Object.freeze({
  TERMS_OF_SERVICE: 'TERMS_OF_SERVICE',
  SERVICE_OPERATION_POLICY: 'SERVICE_OPERATION_POLICY',
  PRIVACY_POLICY: 'PRIVACY_POLICY',
  PUSH: 'PUSH',
})

export const role = Object.freeze({
  SUPER_ADMIN: 'ROLE_SUPER_ADMIN',
  USER: 'ROLE_USER',
  VIEWER: 'ROLE_VIEWER',
  CONTENTS_EDITOR: 'ROLE_CONTENTS_EDITOR',
  CONTENTS_MANAGER: 'ROLE_CONTENTS_MANAGER',
  PRODUCT_MANAGER: 'ROLE_PRODUCT_MANAGER',
  CS_MANAGER: 'ROLE_CS_MANAGER',
})

export const uploadType = Object.freeze({
  THUMBNAIL: 'thumbnail',
  IMAGE: 'image',
  VIDEO: 'video',
  LOGO: 'logo',
  LINK: 'link',
  REWARD_THUMBNAIL: 'rewardThumbnail',
  REWARD_IMAGE: 'rewardImage',
  REWARD_VIDEO: 'rewardVideo',
})

export const tableConst = Object.freeze({
  ACTIONS: 'actions',
})

export const ruleType = Object.freeze({
  IMAGE: 'image',
  VIDEO: 'video',
  REQUIRED: 'required',
})

export const s3PathConst = Object.freeze({
  ARTISTS: 'artists',
  ALBUMS: 'albums',
  CARDS: 'cards',
  BANNERS: 'banners',
  DELIMITER: '/',
})

export const errorMsg = Object.freeze({
  CHECK_INPUT: '입력 내용을 확인해주세요.',
  ERROR_WHILE_UPLOADING: '파일 업로드 중 오류가 발생했습니다.',
  FAILED_MODIFY_MEMBER: '멤버 수정에 실패했습니다.',
  DUPLICATED_ARTIST_NAME: '중복된 아티스트 이름입니다.',
})

export const common = Object.freeze({
  EMPTY: '',
  BLOB: 'blob',
})

export const albumState = Object.freeze({
  INVALID: -1,
  WORKING: 1,
  SUBMITTED: 2,
  REJECTED: 3,
  REVIEWING: 4,
  CONFIRMED: 5,
  DEPLOYING: 6,
  DEPLOYED: 7,
})

export const batchState = Object.freeze({
  INVALID: {
    code: -1,
    text: '비정상',
  },
  ON: {
    code: 0,
    text: 'ON',
  },
  WORKING: {
    code: 1,
    text: '작업중',
  },
  OFF: {
    code: 2,
    text: 'OFF',
  },
})

const allEnums = () => {
  return {
    operationType: operationType,
    contentsType: contentsType,
    nfcCardStatus: nfcCardStatus,
    productOrderStatus: productOrderStatus,
    serviceRegion: serviceRegion,
    usage: usage,
    role: role,
    uploadType: uploadType,
    tableConst: tableConst,
    ruleType: ruleType,
    s3PathConst: s3PathConst,
    errorMsg: errorMsg,
    common: common,
    albumState: albumState,
    batchState: batchState,
  }
}

export default allEnums()
