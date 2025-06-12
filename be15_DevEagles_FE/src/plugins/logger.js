/**
 * 환경변수 기반 로그 유틸리티
 * DEV 모드일 때는 debug 레벨, PROD 모드일 때는 error 레벨로 자동 설정
 */

const isDev = import.meta.env.DEV || import.meta.env.VITE_DEV_MODE === 'true';

// 로그 레벨 우선순위
const LOG_LEVELS = {
  debug: 0,
  info: 1,
  warn: 2,
  error: 3,
};

// dev mode에 따라 로그 레벨 자동 결정
// 개발모드: debug 레벨 (모든 로그 출력)
// 프로덕션: error 레벨 (에러만 출력)
const autoLogLevel = isDev ? 'debug' : 'error';

// 수동 오버라이드가 있으면 사용, 없으면 자동 결정된 레벨 사용
const logLevel = import.meta.env.VITE_LOG_LEVEL || autoLogLevel;
const currentLogLevel = LOG_LEVELS[logLevel] || LOG_LEVELS[autoLogLevel];

class Logger {
  constructor(prefix = '') {
    this.prefix = prefix;
  }

  debug(...args) {
    if (currentLogLevel <= LOG_LEVELS.debug) {
      console.log(`[DEBUG]${this.prefix}`, ...args);
    }
  }

  info(...args) {
    if (currentLogLevel <= LOG_LEVELS.info) {
      console.log(`[INFO]${this.prefix}`, ...args);
    }
  }

  warn(...args) {
    if (currentLogLevel <= LOG_LEVELS.warn) {
      console.warn(`[WARN]${this.prefix}`, ...args);
    }
  }

  error(...args) {
    if (currentLogLevel <= LOG_LEVELS.error) {
      console.error(`[ERROR]${this.prefix}`, ...args);
    }
  }

  // 조건부 로그 (개발 모드에서만)
  devLog(...args) {
    if (isDev) {
      console.log(`[DEV]${this.prefix}`, ...args);
    }
  }

  // 성능 측정용 로그
  time(label) {
    if (isDev) {
      console.time(`${this.prefix} ${label}`);
    }
  }

  timeEnd(label) {
    if (isDev) {
      console.timeEnd(`${this.prefix} ${label}`);
    }
  }

  // 현재 설정 정보 출력
  getConfig() {
    return {
      isDev,
      logLevel,
      autoLogLevel,
      currentLogLevel: Object.keys(LOG_LEVELS)[currentLogLevel],
    };
  }
}

// 기본 로거
export const logger = new Logger();

// 모듈별 로거 생성 함수
export function createLogger(moduleName) {
  return new Logger(` [${moduleName}]`);
}

// 개발 모드 확인 함수
export function isDevelopment() {
  return isDev;
}

// 로그 레벨 확인 함수
export function shouldLog(level) {
  return currentLogLevel <= LOG_LEVELS[level];
}

// 초기 설정 로그 (개발 모드에서만)
if (isDev) {
  logger.info('Logger initialized:', logger.getConfig());
}
