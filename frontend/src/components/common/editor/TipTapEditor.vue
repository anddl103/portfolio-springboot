<template>
  <v-container fluid>
    <v-card
      v-if="editor"
      class="editor ma-auto"
      :class="editorHasError ? 'editor__error' : ''"
    >
      <header-bar
        v-if="showHeader && !readOnly"
        class="editor__header"
        :editor="editor"
        :edit-image="attachImage"
      />
      <editor-content
        class="editor__content"
        :editor="editor"
      />
    </v-card>
    <v-input
      class="ma-auto"
      :error="editorHasError"
      :error-messages="errorMsg"
    />
  </v-container>
</template>

<script>
  import { Editor, EditorContent } from '@tiptap/vue-2'
  import StarterKit from '@tiptap/starter-kit'
  import HeaderBar from '@/components/common/editor/Header'
  import Placeholder from '@tiptap/extension-placeholder'

  import Image from '@tiptap/extension-image'
  import editorMixin from '@/mixins/editorMixin'
  export default {
    name: 'Editor',
    components: {
      EditorContent,
      HeaderBar,
    },
    mixins: [
      editorMixin,
    ],
    props: {
      value: {
        type: String,
        default: '',
      },
      attachImage: {
        type: Boolean,
        default: false,
      },
      readOnly: {
        type: Boolean,
        default: false,
      },
      showHeader: {
        type: Boolean,
        default: true,
      },
      hasError: {
        type: Boolean,
        default: false,
      },
    },
    data: () => ({
      editor: null,
      errorMsg: '',
    }),
    watch: {
      value (value) {
        // HTML
        const isSame = this.editor.getHTML() === value

        // JSON
        // const isSame = this.editor.getJSON().toString() === value.toString()
        if (isSame) {
          return
        }
        this.editor.commands.setContent(this.value, false)
      },
      readOnly () {
        this.editor.options.editable = !this.readOnly
      },
      hasError () {
        this.handleEditorError(this.hasError)
        if (this.hasError) {
          if (this.checkNoContents(this.editor.getHTML())) {
            this.setErrorMsgWhenEmpty()
          }
        } else {
          this.errorMsg = ''
        }
      },
    },
    mounted () {
      Placeholder.options.placeholder = '내용을 입력하세요.'
      this.editor = new Editor({
        editable: !this.readOnly,
        extensions: [
          StarterKit,
          Image,
          Placeholder,
        ],
        content: this.value,
        onUpdate: () => {
          // HTML
          this.$emit('input', this.editor.getHTML())

          // JSON
          // this.$emit('input', this.editor.getJSON())

          if (this.checkNoContents(this.editor.getHTML())) {
            this.setErrorMsgWhenEmpty()
            this.$emit('setHasError', true)
          } else {
            this.errorMsg = ''
            this.$emit('setHasError', false)
          }
        },
      })
    },
    beforeDestroy () {
      this.editor.destroy()
    },
    methods: {
      setErrorMsgWhenEmpty () {
        if (this.editor.isEmpty) {
          this.errorMsg = '필수 항목입니다.'
        } else {
          this.errorMsg = '공백 이외의 문자가 포함되어야 합니다.'
        }
      },
    },
  }
</script>
<style lang="scss">

.ProseMirror {
  > * + * {
    margin-top: 0.75em;
  }
  &:focus {
    outline: none;
  }

  ul,
  ol {
    padding: 0 1rem;
  }

  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    line-height: 1.1;
  }

  pre {
    background: #0D0D0D;
    color: #FFF;
    font-family: 'JetBrainsMono', monospace;
    padding: 0.75rem 1rem;
    border-radius: 0.5rem;
    code {
      color: inherit;
      padding: 0;
      background: none !important;
      font-size: 0.8rem;
    }
  }

  code {
    background-color: rgba(77,77,77,7) !important;
    color: #616161;
  }

  mark {
    background-color: #FAF594;
  }

  img {
    max-width: 100%;
    height: auto;
  }

  hr {
    margin: 1rem 0;
    background-color: #999999;
  }

  blockquote {
    padding-left: 1rem;
    border-left: 2px solid rgba(#FFFFFF, 0.5);
    background-color: #444444;
  }

  hr {
    border: none;
    border-top: 2px solid rgba(#0D0D0D, 0.1);
    margin: 2rem 0;
  }

  ul[data-type="taskList"] {
    list-style: none;
    padding: 0;

    li {
      display: flex;
      align-items: center;

      > label {
        flex: 0 0 auto;
        margin-right: 0.5rem;
      }
    }
  }
}

</style>
<style lang="scss" scoped>
::v-deep {
  /* Basic editor styles */
  .ProseMirror {
    > * + * {
      margin-top: 0.75em;
    }
  }

  /* Placeholder (at the top) */
  .ProseMirror p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: #ced4da;
    pointer-events: none;
    height: 0;
  }

  /* Placeholder (on every new line) */
  /*.ProseMirror p.is-empty::before {
    content: attr(data-placeholder);
    float: left;
    color: #ced4da;
    pointer-events: none;
    height: 0;
  }*/
}

.editor {
  display: flex;
  flex-direction: column;
  max-height: 400px;
  border: 2px solid #555555;
  border-radius: 0.75rem;

  &__error {
    border: 2px solid #ff5252;
  }

  &__header {
    display: flex;
    align-items: center;
    flex: 0 0 auto;
    flex-wrap: wrap;
    padding: 0.25rem;
    border-bottom: 2px solid #555555;
  }

  &__content {
    padding: 1.25rem 1rem;
    flex: 1 1 auto;
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
  }

  /* Some information about the status */
  &__status {
    display: flex;
    align-items: center;
    border-radius: 5px;

    &::before {
      content: ' ';
      flex: 0 0 auto;
      display: inline-block;
      width: 0.5rem;
      height: 0.5rem;
      background: rgba(#0D0D0D, 0.5);
      border-radius: 50%;
      margin-right: 0.5rem;
    }

    &--connecting::before {
      background: #616161;
    }

    &--connected::before {
      background: #B9F18D;
    }
  }

  &__name {
    button {
      background: none;
      border: none;
      font: inherit;
      font-size: 12px;
      font-weight: 600;
      color: #0D0D0D;
      border-radius: 0.4rem;
      padding: 0.25rem 0.5rem;

      &:hover {
        color: #FFF;
        background-color: #0D0D0D;
      }
    }
  }
}
</style>
