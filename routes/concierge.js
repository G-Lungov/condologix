var express = require('express');
var router = express.Router();

/* GET concierge page. */
router.get('/', function(req, res, next) {
  res.render('../views/concierge', { title: 'Concierge' });
});

module.exports = router;
