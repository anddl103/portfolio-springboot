import { isEmpty, isNil, forEach, cloneDeep } from 'lodash'
import I18Dialog from '@/components/common/I18Dialog.vue'

const AddMixin = {
    data: () => ({
        valid: {},
        message: [],
        // item: {},
        i18: false,
        i18key: '',
        i18EditedItem: {},
    }),
    created () {
        this.reset()
    },

    computed: {
        localDialog: {
            get () {
                return this.dialog
            },
            set (val) {
                this.$emit('update:dialog', val)
            },
        },

    },
    methods: {
        reset (cloneItem) {
            const { createFormats } = this
            if (isEmpty(createFormats) || isNil(createFormats)) return
            console.log(createFormats)
            const item = {}
            const valid = {}
            forEach(createFormats.items, (itemFormat) => {
                const keys = itemFormat.key.split('.')
                if (itemFormat.key === 'title' || (itemFormat.key === 'description' && itemFormat.type === 'text') || (itemFormat.key === 'subDescription' && itemFormat.type === 'text') || itemFormat.key === 'copyright') {
                    item[itemFormat.key] = { ko: '', en: '', ja: '' } // es: "", , zh: ""
                } else if (itemFormat.key === 'options') {
                    item[itemFormat.key] = { hasWallpaper: false, hasWidget: false }
                } else if (itemFormat.key === 'contents') {
                    item[itemFormat.key] = { themeId: '', galleryId: '' }
                } else if (keys.length > 1) {
                    this.targetArrayKeyDefaultValue(item, itemFormat.key, itemFormat.value)
                } else {
                    if (!isNil(itemFormat.value)) item[itemFormat.key] = itemFormat.value
                    else item[itemFormat.key] = ''
                }
                if (keys.length > 1) {
                    const defaultValue = {
                        errors: [],
                        validList: Object.keys(itemFormat.validate),
                    }
                    this.targetArrayKeyDefaultValue(valid, itemFormat.key, defaultValue)
                } else {
                    valid[itemFormat.key] = {
                        errors: [],
                        validList: Object.keys(itemFormat.validate),
                    }
                }
            })
            console.log(item)
            if (cloneItem) {
                if (isNil(cloneItem.subDescription) || isEmpty(cloneItem.subDescription)) {
                    cloneItem.subDescription = { ko: '', en: '', ja: '' }
                }
            }

            this.valid = cloneDeep(valid)
            this.item = (cloneItem || cloneDeep(item))
        },
        async formValidate () {
            console.log(
                '------------------------------------ str formValidate ------------------------------------',
            )
            const { item } = this
            this.$v.$touch()
            const validate = this.$v.$invalid

            if (validate) {
                forEach(this.valid, (i, key) => {
                    console.log(key)
                    this.checkValidate(key)
                })
                return false
            }
            console.log(
                '------------------------------------ end formValidate ------------------------------------',
            )
            return true
        },
        i18Change (key) {
            this.i18 = !this.i18
            this.i18key = key
            this.i18EditedItem = this.item[key]
        },
        validErrors (key) {
            const valid = this.targetArrayKeyReturn(this.$v.item, key)
            const itemValid = this.targetArrayKeyReturn(this.valid, key)

            const errors = []
            for (const v of itemValid.validList) {
                if (v === 'email') !valid[v] && errors.push('email 타입이 아닙니다. ')
                if (v === 'required') !valid[v] && errors.push('필수항목입니다.')
                if (v === 'minLength') !valid[v] && errors.push('8글자 이상입니다.')
                if (v === 'sameAs') { !valid[v] && errors.push('같은 패스워드가 아닙니다.') }
                if (v === 'url') !valid[v] && errors.push('url 형식이 아닙니다.')
                if (v === 'isUnique') !valid[v] && errors.push('E-mail이 이미 있습니다.')
                if (v === 'isUniqueName') !valid[v] && errors.push('이름을 사용하고 있습니다.')
                if (v === 'isTypeRequired') !valid[v] && errors.push('필수항목입니다.')
            }
            return errors
        },
        checkValidate (key) {
            const valid = this.targetArrayKeyReturn(this.$v.item, key)
            valid.$touch()
        },
        checkDuplicate (key) {
            const message = []
            const valid = this.$v.item[key]
            valid.$touch()
            valid.isUnique && message.push('사용 가능합니다.')
            this.message = message
        },
        targetArrayKeyReturn (target, key) {
            let tmpKey = target
            const keys = key.split('.')
            keys.forEach((exKey) => {
                tmpKey = tmpKey[exKey]
            })
            return tmpKey
        },
        targetArrayKeyDefaultValue (target, key, value) {
            let tmpKey = target
            const keys = key.split('.')
            keys.forEach((exKey, i) => {
                if (!tmpKey[exKey]) {
                    if (keys.length === (i + 1)) {
                        tmpKey[exKey] = value || ''
                    } else {
                        tmpKey[exKey] = {}
                    }
                }
                tmpKey = tmpKey[exKey]
            })
        },
    },
    validations () {
        const { createFormats } = this
        const validate = {}

        forEach(createFormats.items, (itemFormat) => {
            let tmpKey = validate
            const keys = itemFormat.key.split('.')
            keys.forEach((exKey, i) => {
                if (!tmpKey[exKey]) {
                    if (keys.length === (i + 1)) {
                        tmpKey[exKey] = itemFormat.validate
                    } else {
                        tmpKey[exKey] = {}
                    }
                }
                tmpKey = tmpKey[exKey]
            })
        })

        return { item: validate }
    },
}

export default AddMixin
