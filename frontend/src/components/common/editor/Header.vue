<template>
  <div>
    <template v-for="(item, index) in headers">
      <div
        v-if="item.type === 'divider'"
        :key="index"
        class="divider"
      />
      <header-item
        v-else
        :key="index"
        v-bind="item"
      />
    </template>
    <file-selector
      class="ma-3"
      :open="openSelector"
      :disabled="false"
      :width="150"
      :height="150"
      @attach="handleFile"
      @close="closeSelector"
    />
  </div>
</template>

<script>
  import HeaderItem from '@/components/common/editor/HeaderItem'
  import FileSelector from './FileSelector'

  export default {
    name: 'HeaderBar',
    components: {
      HeaderItem,
      FileSelector,
    },
    props: {
      editor: {
        type: Object,
        required: true,
      },
      editImage: {
        type: Boolean,
        default: false,
      },
    },
    data () {
      return {
        openSelector: false,
        attachedFiles: [],
        file: null,
        fileUrl: '',
        items: [
          {
            icon: 'mdi-undo',
            title: 'Undo',
            action: () => this.editor.chain().focus().undo().run(),
          },
          {
            icon: 'mdi-redo',
            title: 'Redo',
            action: () => this.editor.chain().focus().redo().run(),
          },
          {
            icon: 'mdi-format-clear',
            title: 'Clear Format',
            action: () => this.editor.chain()
              .focus()
              .clearNodes()
              .unsetAllMarks()
              .run(),
          },
          {
            type: 'divider',
          },
          {
            icon: 'mdi-format-bold',
            title: 'Bold',
            action: () => this.editor.chain().focus().toggleBold().run(),
            isActive: () => this.editor.isActive('bold'),
          },
          {
            icon: 'mdi-format-italic',
            title: 'Italic',
            action: () => this.editor.chain().focus().toggleItalic().run(),
            isActive: () => this.editor.isActive('italic'),
          },
          {
            icon: 'mdi-format-strikethrough-variant',
            title: 'Strike',
            action: () => this.editor.chain().focus().toggleStrike().run(),
            isActive: () => this.editor.isActive('strike'),
          },
          {
            icon: 'mdi-code-tags',
            title: 'Code',
            action: () => this.editor.chain().focus().toggleCode().run(),
            isActive: () => this.editor.isActive('code'),
          },
          {
            type: 'divider',
          },
          {
            icon: 'mdi-format-header-1',
            title: 'Heading 1',
            action: () => this.editor.chain().focus().toggleHeading({ level: 1 }).run(),
            isActive: () => this.editor.isActive('heading', { level: 1 }),
          },
          {
            icon: 'mdi-format-header-2',
            title: 'Heading 2',
            action: () => this.editor.chain().focus().toggleHeading({ level: 2 }).run(),
            isActive: () => this.editor.isActive('heading', { level: 2 }),
          },
          {
            icon: 'mdi-format-paragraph',
            title: 'Paragraph',
            action: () => this.editor.chain().focus().setParagraph().run(),
            isActive: () => this.editor.isActive('paragraph'),
          },
          {
            icon: 'mdi-format-quote-open',
            title: 'Blockquote',
            action: () => this.editor.chain().focus().toggleBlockquote().run(),
            isActive: () => this.editor.isActive('blockquote'),
          },
          {
            icon: 'mdi-code-not-equal-variant',
            title: 'Code Block',
            action: () => this.editor.chain().focus().toggleCodeBlock().run(),
            isActive: () => this.editor.isActive('codeBlock'),
          },
          {
            type: 'divider',
          },
          {
            icon: 'mdi-format-list-bulleted',
            title: 'Bullet List',
            action: () => this.editor.chain().focus().toggleBulletList().run(),
            isActive: () => this.editor.isActive('bulletList'),
          },
          {
            icon: 'mdi-format-list-numbered',
            title: 'Ordered List',
            action: () => this.editor.chain().focus().toggleOrderedList().run(),
            isActive: () => this.editor.isActive('orderedList'),
          },
          {
            type: 'divider',
          },
          {
            icon: 'mdi-minus',
            title: 'Horizontal Rule',
            action: () => this.editor.chain().focus().setHorizontalRule().run(),
          },
          {
            icon: 'mdi-wrap',
            title: 'Hard Break',
            action: () => this.editor.chain().focus().setHardBreak().run(),
          },
        ],
      }
    },
    computed: {
      headers () {
        const arr = this.items
        if (this.editImage) {
          const imageObj = {
            icon: 'mdi-image',
            title: 'Image',
            action: () => { this.openSelector = true },
          }
          arr.push(imageObj)
        }
        return arr
      },
    },
    watch: {
      file () {
        if (this.fileUrl) {
          // const url = URL.createObjectURL(this.file)
          this.editor.chain().focus().setImage({ src: this.fileUrl, alt: 'test' }).run()
        }
      },
    },
    methods: {
      closeSelector () {
        this.openSelector = false
      },
      handleFile (obj) {
        this.attachedFiles.push(obj)
        this.file = obj.file
        this.fileUrl = obj.url
      },
    },
  }
</script>

<style lang="scss" scoped>
.divider {
  width: 2px;
  height: 1.25rem;
  background-color: rgba(#000000, 0.1);
  margin-left: 0.5rem;
  margin-right: 0.75rem;
}
</style>
