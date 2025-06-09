module.exports = {
  root: true,
  extends: '@react-native-community',
  env: {
    es2019: true,
    node: true,
  },
  rules: {
    semi: ['error', 'always'],
    quotes: ['warn', 'single'],
    indent: ['error', 2],
    'comma-dangle': ['error', 'always-multiline'],
    eqeqeq: ['error', 'always', { null: 'ignore' }],
    'object-curly-newline': [
      'error',
      { multiline: true, minProperties: 80, consistent: true },
    ],
    'object-curly-spacing': ['error', 'always'],
    'react-native/no-inline-styles': 0,
  },
};
