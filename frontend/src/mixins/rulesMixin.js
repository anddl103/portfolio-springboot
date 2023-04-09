const rulesMixin = {
  data: () => ({
    numberOnly: [
      v => !!v || '필수 항목입니다.',
      v => /^\d+$/.test(v) || '숫자만 사용할 수 있습니다.',
    ],
    quantityRules: [
      v => !!v || '필수 항목입니다.',
      v => /^\d+$/.test(v) || '숫자만 사용할 수 있습니다.',
      v => v > 0 || '수량은 0보다 커야 합니다.',
    ],
  }),
  methods: {
    required (value) {
      if (value instanceof Array && value.length === 0) {
        return '필수 항목입니다.'
      }
      return !!value || '필수 항목입니다.'
    },
    timeFormat (value) {
      const moment = require('moment')
      const aDate = moment(value, 'YYYY-MM-DD HH:mm', true)
      if (!aDate.isValid()) {
        return '\'YYYY-MM-DD HH:mm\' 형식으로 작성해주세요.'
      } else {
        return true
      }
    },
    checkForm (form) {
      return form.validate()
    },
    resetForm (form) {
      form.reset()
    },
    resetFormValidation (form) {
      form.resetValidation()
    },
    checkChanged (obj1, obj2) {
      return JSON.stringify(obj1) !== JSON.stringify(obj2)
    },
  },
}

export default rulesMixin
