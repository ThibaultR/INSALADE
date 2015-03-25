<?php
namespace Insalade\UserBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Insalade\UserBundle\Entity\Asso;
use Symfony\Component\DependencyInjection\ContainerInterface;

class RegistrationListener
{
    private $container;

    public function __construct(ContainerInterface $container) {
        $this->container = $container;
    }

    public function postPersist(LifecycleEventArgs $args)
    {
        $entity = $args->getEntity();
        $entityManager = $args->getEntityManager();

        // peut-être voulez-vous seulement agir sur une entité « Product »
        if ($entity instanceof User) {
            $entity->setEnabled(false);
        }

        if ($entity instanceof Asso) {
            $entity->setEnabled(false);
            $entity->setParentId(2);
        }

        if ($entity instanceof Amicale) {
            $entity->setEnabled(true);
        }

        if ($entity instanceof Insalade) {
            $entity->setEnabled(true);
        }

        $entityManager->flush();
    }
}