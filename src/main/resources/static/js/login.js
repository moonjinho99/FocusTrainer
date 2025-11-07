async function login() {
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value.trim();

  if (!email || !password) {
    alert('이메일과 비밀번호를 모두 입력해주세요.');
    return;
  }

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      alert(result.message || '로그인 실패');
      return;
    }

    const { accessToken, refreshToken, nickname } = result.data;

    // JWT 토큰 로컬 저장
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('nickname', nickname);

    alert('로그인 성공');

    // JWT를 Authorization 헤더에 담아 메인 페이지 요청
    const htmlResponse = await fetch('/focus/main', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });

    if (htmlResponse.status === 401) {
      alert('세션이 만료되었거나 인증되지 않았습니다.');
      localStorage.clear();
      window.location.href = '/auth/login';
      return;
    }

    // 응답받은 HTML로 화면 렌더링
    const html = await htmlResponse.text();
    document.open();
    document.write(html);
    document.close();

  } catch (error) {
    console.error('로그인 요청 오류:', error);
    alert('서버 오류가 발생했습니다.');
  }
}
