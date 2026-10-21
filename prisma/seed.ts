import { PrismaClient } from '@prisma/client'
import { hashPassword } from '@/lib/utils-wifi'

const prisma = new PrismaClient()

async function main() {
  console.log('Seeding database...')

  // Create packages
  const packages = await Promise.all([
    prisma.package.create({
      data: {
        name: '2.5 Hour Access',
        price: 10,
        duration: 2.5,
        description: '2.5 hour of high-speed internet access'
      }
    }),
    prisma.package.create({
      data: {
        name: '12 Hours Access',
        price: 15,
        duration: 12,
        description: '12 hours of high-speed internet access'
      }
    }),
    prisma.package.create({
      data: {
        name: '24 Hours Access',
        price: 20,
        duration: 24,
        description: '24 hours of high-speed internet access'
      }
    }),
    prisma.package.create({
      data: {
        name: '3 Days Access',
        price: 70,
        duration: 72,
        description: '3 days of high-speed internet access'
      }
    })
  ])

  console.log('Created packages:', packages.map(p => p.name))

  // Create admin user
  const adminPassword = await hashPassword('admin123')
  const admin = await prisma.admin.create({
    data: {
      username: 'admin',
      password: adminPassword,
      email: 'admin@wifibilling.com'
    }
  })

  console.log('Created admin user:', admin.username)
  console.log('Database seeded successfully!')
}

main()
  .catch((e) => {
    console.error('Seeding failed:', e)
    process.exit(1)
  })
  .finally(async () => {
    await prisma.$disconnect()
  })
