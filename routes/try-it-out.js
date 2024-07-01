var express = require('express');
var router = express.Router();

/* GET try-it-out page. */
router.get('/', function(req, res, next) {
  res.render('../views/try-it-out', { title: 'Try It Out' });
});

module.exports = router;
