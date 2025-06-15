<template>
  <div class="card" :class="cardClasses">
    <!-- Card Header -->
    <div v-if="$slots.header || title || subtitle" class="card-header">
      <slot name="header">
        <h3 v-if="title" class="card-title">{{ title }}</h3>
        <p v-if="subtitle" class="card-subtitle">{{ subtitle }}</p>
      </slot>
    </div>

    <!-- Card Body -->
    <div class="card-body" :class="{ 'no-padding': noPadding }">
      <slot></slot>
    </div>

    <!-- Card Footer -->
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BaseCard',
    props: {
      title: {
        type: String,
        default: '',
      },
      subtitle: {
        type: String,
        default: '',
      },
      shadow: {
        type: String,
        default: '',
        validator: value => ['', 'sm', 'lg'].includes(value),
      },
      noPadding: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      cardClasses() {
        const classes = [];
        if (this.shadow) {
          classes.push(
            `shadow${this.shadow === 'sm' || this.shadow === 'lg' ? '-' + this.shadow : ''}`
          );
        }
        return classes;
      },
    },
  };
</script>

<style scoped>
  .card-body.no-padding {
    padding: 0;
  }
</style>
