let verified = false;

// 인증메일 보내기
function sendMail() {
  const email = document.getElementById('email').value.trim();
  if (!email) {
    alert('이메일을 입력하세요.');
    return;
  }

  fetch('/api/auth/mail/send', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        alert('인증메일이 발송되었습니다.');
        document.getElementById('verifySection').style.display = 'flex';
      } else {
        alert(data.message || '메일 발송 실패');
      }
    })
    .catch(() => alert('서버 오류가 발생했습니다.'));
}

// 인증번호 확인
function codeCheck() {
  const email = document.getElementById('email').value.trim();
  const code = document.getElementById('verifyCode').value.trim();

  if (!code) {
    alert('인증번호를 입력하세요.');
    return;
  }

  fetch('/api/auth/code/check', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, code })
  })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        alert('이메일 인증이 완료되었습니다.');
        verified = true;
        document.getElementById('verifySection').style.display = 'none';
        document.getElementById('sendMailBtn').textContent = '인증완료';
        document.getElementById('sendMailBtn').disabled = true;
      } else {
        alert(data.message || '인증번호가 일치하지 않습니다.');
      }
    })
    .catch(() => alert('서버 오류가 발생했습니다.'));
}

// 회원가입 전 유효성 검사
function validateBeforeSubmit() {
  if (!verified) {
    alert('이메일 인증을 완료해야 회원가입이 가능합니다.');
    return false;
  }
  return true;
}

//회원가입
function signup(e) {
  e.preventDefault(); // 기본 폼 제출 막기

  const email = document.getElementById('email').value.trim();
  const password = document.querySelector('[name=password]').value.trim();
  const nickname = document.querySelector('[name=nickname]').value.trim();

  if (!email || !password || !nickname) {
    alert('모든 필드를 입력하세요.');
    return;
  }

  if (!verified) {
    alert('이메일 인증을 완료해야 합니다.');
    return;
  }

  const data = { email, password, nickname };

  fetch('/api/auth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
    .then(res => res.json())
    .then(resp => {
      if (resp.success) {
        alert('회원가입이 완료되었습니다.');
        window.location.href = '/auth/login';
      } else {
        alert(resp.message || '회원가입 실패');
      }
    })
    .catch(() => alert('서버 오류가 발생했습니다.'));
}
